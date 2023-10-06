package ru.practicum.main_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatisticClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatistic;
import ru.practicum.main_service.dto.eventDto.*;
import ru.practicum.main_service.exeptions.ConflictParametersException;
import ru.practicum.main_service.model.event.StateActionForUser;
import ru.practicum.main_service.exeptions.BadParametersException;
import ru.practicum.main_service.exeptions.NotFoundException;
import ru.practicum.main_service.exeptions.AlreadyCreatedException;
import ru.practicum.main_service.mapper.EventMapper;
import ru.practicum.main_service.model.CategoryEntity;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.model.event.EventEntity;
import ru.practicum.main_service.model.event.EventState;
import ru.practicum.main_service.model.event.StateActionForAdmin;
import ru.practicum.main_service.repository.CategoryRepository;
import ru.practicum.main_service.repository.EventRepository;
import ru.practicum.main_service.repository.UserRepository;
import ru.practicum.main_service.service.EventService;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.main_service.MainServiceApplication.DATE_TIME_FORMAT;
import static ru.practicum.main_service.MainServiceApplication.DATE_TIME_FORMATTER;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EntityManager entityManager;
    private final StatisticClient statisticClient;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        CategoryEntity category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория добавляемого события не найдена"));
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadParametersException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value:" + eventDate);
        }
        EventEntity event = eventMapper.fromNewEventToEventEntity(newEventDto);
        event.setCreatedOn(LocalDateTime.now());
        event.setCategory(category);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Can't create event, the user with id = %s doesn't exist", userId)));
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        EventFullDto addedEvent = eventMapper.fromEntityToEventFullDto(eventRepository.save(event));
        log.info("Создано событие c id {}", addedEvent.getId());
        return addedEvent;
    }

    @Override
    public List<EventShortDto> getEventsByCurrentUserID(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventMapper.toEventShortDtoList(eventRepository.findAllByInitiatorId(userId, page));
    }

    private EventEntity getEventByIds(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id %d " +
                        "и id пользователя %d не найдено", eventId, userId)));
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        return eventMapper.fromEntityToEventFullDto(getEventByIds(userId, eventId));
    }

    @Override
    public EventFullDto updateEventByCurrentUserIdAndEventId(Long userId, Long eventId, UpdateEventUserDto updateEvent) {
        EventEntity event = getEventByIds(userId, eventId);
        if (event.getPublishedOn() != null) {
            throw new AlreadyCreatedException("Event already published");
        }
        if (updateEvent == null) {
            return eventMapper.fromEntityToEventFullDto(event);
        }
        if (updateEvent.getEventDate() != null) {
            LocalDateTime eventDateTime = updateEvent.getEventDate();
            if (eventDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadParametersException("The start date of the event to be modified is less than one hour from the publication date.");
            }
            event.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getCategory() != null) {
            CategoryEntity category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Не найдено категории по индексу, при обновлении события"));
            event.setCategory(category);
        }
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateActionForUser.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        return eventMapper.fromEntityToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsWithSort(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                 String rangeEnd, Boolean onlyAvailable, String sort,
                                                 Integer from, Integer size, HttpServletRequest request) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
        }
        checkDateTime(start, end);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);

        Root<EventEntity> root = query.from(EventEntity.class);
        Predicate criteria = builder.conjunction();

        if (text != null) {
            Predicate annotationContain = builder.like(builder.lower(root.get("annotation")),
                    "%" + text.toLowerCase() + "%");
            Predicate descriptionContain = builder.like(builder.lower(root.get("description")),
                    "%" + text.toLowerCase() + "%");
            Predicate containText = builder.or(annotationContain, descriptionContain);

            criteria = builder.and(criteria, containText);
        }

        if (categories != null && categories.size() > 0) {
            Predicate containStates = root.get("category").in(categories);
            criteria = builder.and(criteria, containStates);
        }

        if (paid != null) {
            Predicate isPaid;
            if (paid) {
                isPaid = builder.isTrue(root.get("paid"));
            } else {
                isPaid = builder.isFalse(root.get("paid"));
            }
            criteria = builder.and(criteria, isPaid);
        }

        if (rangeStart != null) {
            Predicate greaterTime = builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start);
            criteria = builder.and(criteria, greaterTime);
        }
        if (rangeEnd != null) {
            Predicate lessTime = builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end);
            criteria = builder.and(criteria, lessTime);
        }

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));
        List<EventEntity> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (onlyAvailable) {
            events = events.stream()
                    .filter((event -> event.getConfirmedRequests() < (long) event.getParticipantLimit()))
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                events = events.stream().sorted(Comparator.comparing(EventEntity::getEventDate)).collect(Collectors.toList());
            } else {
                events = events.stream().sorted(Comparator.comparing(EventEntity::getViews)).collect(Collectors.toList());
            }
        }

        if (events.size() == 0) {
            return new ArrayList<>();
        }

        setView(events);
        sendStat(events, request);
        return eventMapper.toEventShortDtoList(events);
    }


    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        EventEntity event = eventRepository.findByIdAndPublishedOnIsNotNull(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Can't find event with id = %s event doesn't exist", id)));
        event.setViews(event.getViews() + 1);
        setView(event);
        sendStat(event, request);
        return eventMapper.fromEntityToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminDto updateEventAdminDto) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Can't update event with id = %s", eventId)));
        if (updateEventAdminDto == null) {
            return eventMapper.fromEntityToEventFullDto(event);
        }
        if (updateEventAdminDto.getCategory() != null) {
            CategoryEntity category = categoryRepository
                    .findById(updateEventAdminDto.getCategory()).orElseThrow(() -> new NotFoundException(""));
            event.setCategory(category);
        }
        if (updateEventAdminDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminDto.getAnnotation());
        }
        if (updateEventAdminDto.getDescription() != null) {
            event.setDescription(updateEventAdminDto.getDescription());
        }
        if (updateEventAdminDto.getLocation() != null) {
            event.setLocation(updateEventAdminDto.getLocation());
        }
        if (updateEventAdminDto.getPaid() != null) {
            event.setPaid(updateEventAdminDto.getPaid());
        }
        if (updateEventAdminDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminDto.getParticipantLimit());
        }
        if (updateEventAdminDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminDto.getRequestModeration());
        }
        if (updateEventAdminDto.getTitle() != null) {
            event.setTitle(updateEventAdminDto.getTitle());
        }
        if (updateEventAdminDto.getStateAction() != null) {
            if (updateEventAdminDto.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
                if (event.getPublishedOn() != null) {
                    throw new AlreadyCreatedException("Event already published");
                }
                if (event.getState() != null && event.getState().equals(EventState.CANCELED)) {
                    throw new ConflictParametersException("Event already canceled");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventAdminDto.getStateAction().equals(StateActionForAdmin.REJECT_EVENT)) {
                if (event.getPublishedOn() != null) {
                    throw new AlreadyCreatedException("Event already published");
                }
                event.setState(EventState.CANCELED);
            }
        }
        if (updateEventAdminDto.getEventDate() != null) {
            LocalDateTime eventDateTime = updateEventAdminDto.getEventDate();
            if (eventDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadParametersException("The start date of the event to be modified is less than one hour from the publication date.");
            }
            event.setEventDate(updateEventAdminDto.getEventDate());
        }
        return eventMapper.fromEntityToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getEventsWithParamsByAdmin(List<Long> users, List<EventState> states,
                                                         List<Long> categoriesId, String rangeStart, String rangeEnd,
                                                         Integer from, Integer size) {
        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER) : null;
        LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER) : null;

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);

        Root<EventEntity> root = query.from(EventEntity.class);
        Predicate criteria = builder.conjunction();

        if (categoriesId != null && !categoriesId.isEmpty()) {
            Predicate containCategories = root.get("category").in(categoriesId);
            criteria = builder.and(criteria, containCategories);
        }
        if (users != null && !users.isEmpty()) {
            Predicate containUsers = root.get("initiator").in(users);
            criteria = builder.and(criteria, containUsers);
        }
        if (states != null && !states.isEmpty()) {
            Predicate containStates = root.get("state").in(states);
            criteria = builder.and(criteria, containStates);
        }
        if (rangeStart != null) {
            Predicate greaterTime = builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start);
            criteria = builder.and(criteria, greaterTime);
        }
        if (rangeEnd != null) {
            Predicate lessTime = builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end);
            criteria = builder.and(criteria, lessTime);
        }

        query.select(root).where(criteria);
        List<EventEntity> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        setView(events);
        return events.stream().map(eventMapper::fromEntityToEventFullDto).collect(Collectors.toList());
    }

    private void checkDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            start = LocalDateTime.now().minusYears(100);
        }
        if (end == null) {
            end = LocalDateTime.now();
        }
        if (start.isAfter(end)) {
            throw new BadParametersException("Некорректный запрос. Дата окончания события задана позже даты старта");
        }
    }

    public void sendStat(EventEntity event, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String remoteAddr = request.getRemoteAddr();
        String nameService = "main-service";

        EndpointHitDto requestDto = new EndpointHitDto();
        requestDto.setTimestamp(now);
        requestDto.setUri("/events/" + event.getId());
        requestDto.setApp(nameService);
        requestDto.setIp(remoteAddr);

        statisticClient.postStats(requestDto);
    }

    public void sendStat(List<EventEntity> events, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String remoteAddr = request.getRemoteAddr();
        String nameService = "main-service";

        for (EventEntity event : events) {
            EndpointHitDto requestDto = new EndpointHitDto();
            requestDto.setTimestamp(now);
            requestDto.setUri("/events/" + event.getId());
            requestDto.setApp(nameService);
            requestDto.setIp(remoteAddr);
            statisticClient.postStats(requestDto);
        }
    }

    public void setView(List<EventEntity> events) {
        LocalDateTime start = events.get(0).getCreatedOn();
        List<String> uris = new ArrayList<>();
        Map<String, EventEntity> eventsUri = new HashMap<>();
        String uri = "";

        for (EventEntity event : events) {
            if (start.isBefore(event.getCreatedOn())) {
                start = event.getCreatedOn();
            }
            uri = "/events/" + event.getId();
            uris.add(uri);
            eventsUri.put(uri, event);
            event.setViews(0L);
        }

        String startTime = start.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));

        List<ViewStatistic> stats = statisticClient.getStats(startTime, endTime, uris, false);
        stats.forEach((stat) -> eventsUri.get(stat.getUri()).setViews(stat.getHits()));
    }

    public void setView(EventEntity event) {
        String startTime = event.getCreatedOn().format(DATE_TIME_FORMATTER);
        String endTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        List<String> uris = List.of("/events/" + event.getId());

        List<ViewStatistic> stats = statisticClient.getStats(startTime, endTime, uris, false);
        if (stats.size() == 1) {
            event.setViews(stats.get(0).getHits() + 1);
        } else {
            event.setViews(1L);
        }
    }
}
