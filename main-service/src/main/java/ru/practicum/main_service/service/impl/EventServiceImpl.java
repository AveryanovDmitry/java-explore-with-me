package ru.practicum.main_service.service.impl;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatistic;
import ru.practicum.main_service.dto.eventDto.*;
import ru.practicum.main_service.dto.eventDto.updateEventDto.UpdateEventAdminDto;
import ru.practicum.main_service.dto.eventDto.updateEventDto.UpdateEventDto;
import ru.practicum.main_service.dto.eventDto.updateEventDto.UpdateEventUserDto;
import ru.practicum.main_service.exeptions.ConflictParametersException;
import ru.practicum.main_service.mapper.LocationMapper;
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
import ru.practicum.main_service.repository.CommentsRepository;
import ru.practicum.main_service.repository.EventRepository;
import ru.practicum.main_service.repository.UserRepository;
import ru.practicum.main_service.repository.dao.event.EventDao;
import ru.practicum.main_service.service.EventService;
import ru.practicum.main_service.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.main_service.MainServiceApplication.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final StatisticClient statisticClient;
    private final LocationMapper locationMapper;
    private final RequestService requestService;
    private final EventDao eventDao;
    private final CommentsRepository commentsRepository;

    @Value("${app.name}")
    private String appName;

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
        return fromEntityToEventShortDto(eventRepository.findAllByInitiatorId(userId, page));
    }

    private EventEntity getEventByIds(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id %d " +
                        "и id пользователя %d не найдено", eventId, userId)));
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        return fromEntityToEventFullDto(Collections.singleton(getEventByIds(userId, eventId))).get(0);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto updateEvent) {
        EventEntity event;
        if (userId != null) {
            event = getEventByIds(userId, eventId);
        } else {
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException(String.format("Can't update event with id = %s", eventId)));
        }
        if (updateEvent == null) {
            return fromEntityToEventFullDto(Collections.singleton(event)).get(0);
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
        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(updateEvent.getLocation()));
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
        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            event.setTitle(updateEvent.getTitle());
        }
        if (userId != null && updateEvent.getClass() == UpdateEventUserDto.class) {
            return updateEventByCurrentUserIdAndEventId(event, (UpdateEventUserDto) updateEvent);
        } else {
            return updateEventByAdmin(event, (UpdateEventAdminDto) updateEvent);
        }
    }

    private EventFullDto updateEventByAdmin(EventEntity event, UpdateEventAdminDto updateEventAdminDto) {
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
        return fromEntityToEventFullDto(Collections.singleton(eventRepository.save(event))).get(0);
    }

    private EventFullDto updateEventByCurrentUserIdAndEventId(EventEntity event, UpdateEventUserDto updateEvent) {
        if (event.getPublishedOn() != null) {
            throw new AlreadyCreatedException("Event already published");
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateActionForUser.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        return fromEntityToEventFullDto(Collections.singleton(eventRepository.save(event))).get(0);
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

        List<EventEntity> events = eventDao.getEvents(text, categories, paid, rangeStart,
                rangeEnd, from, size, start, end);

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter((event -> requestService.getConfirmedRequests(
                                    Collections.singleton(event.getId()))
                            .getOrDefault(event.getId(), 0L) < event.getParticipantLimit()))
                    .collect(Collectors.toList());
        }

        if (sort != null && sort.equals("EVENT_DATE")) {
            events = events.stream().sorted(Comparator.comparing(EventEntity::getEventDate)).collect(Collectors.toList());
        }

        if (events.size() == 0) {
            return new ArrayList<>();
        }

        sendStat(request.getRequestURI(), request.getRemoteAddr());
        return fromEntityToEventShortDto(events);
    }


    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        EventEntity event = eventRepository.findByIdAndPublishedOnIsNotNull(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Can't find event with id = %s event doesn't exist", id)));
        sendStat(request.getRequestURI(), request.getRemoteAddr());
        return fromEntityToEventFullDto(Collections.singleton(event)).get(0);
    }


    @Override
    public List<EventFullDto> getEventsWithParamsByAdmin(List<Long> users, List<EventState> states,
                                                         List<Long> categoriesId, String rangeStart, String rangeEnd,
                                                         Integer from, Integer size) {
        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER) : null;
        LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER) : null;

        List<EventEntity> events = eventDao.getEventsByUsers(users, states, categoriesId, rangeStart, rangeEnd,
                from, size, start, end);

        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        return fromEntityToEventFullDto(events);
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

    private void sendStat(String uri, String ip) {
        EndpointHitDto endpointHitRequestDto = EndpointHitDto.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        ResponseEntity<EndpointHitDto> response = statisticClient.postStats(endpointHitRequestDto);
        log.info("Сохранили в статистике обращение {}", response);
    }

    private List<EventFullDto> fromEntityToEventFullDto(Collection<EventEntity> events) {
        List<Long> ids = events.stream()
                .map(EventEntity::getId)
                .collect(Collectors.toList());

        List<EventFullDto> listFullEventDto = events.stream()
                .map(eventMapper::fromEntityToEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = getViews(ids);
        Map<Long, Long> confirmedRequests = requestService.getConfirmedRequests(ids);

        listFullEventDto.forEach(eventFullDto -> {
            eventFullDto.setViews(eventsViews.getOrDefault(eventFullDto.getId(), 0L));
            eventFullDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventFullDto.getId(), 0L));
        });

        return listFullEventDto;
    }

    private List<EventShortDto> fromEntityToEventShortDto(Collection<EventEntity> events) {
        List<Long> ids = events.stream()
                .map(EventEntity::getId)
                .collect(Collectors.toList());

        List<EventShortDto> litShortEventDto = eventMapper.toEventShortDtoList((List<EventEntity>) events);

        Map<Long, Long> countComments = new HashMap<>();
        commentsRepository.getCountCommentsByIds(ids)
                .forEach(comment -> countComments.put(comment.getId(), comment.getCount()));

        Map<Long, Long> eventsViews = getViews(ids);
        Map<Long, Long> confirmedRequests = requestService.getConfirmedRequests(ids);

        litShortEventDto.forEach(shortEvent -> {
            shortEvent.setViews(eventsViews.getOrDefault(shortEvent.getId(), 0L));
            shortEvent.setConfirmedRequests(confirmedRequests.getOrDefault(shortEvent.getId(), 0L));
            shortEvent.setCountComments(countComments.getOrDefault(shortEvent.getId(), 0L));
        });

        return litShortEventDto;
    }

    private Map<Long, Long> getViews(Collection<Long> ids) {
        Set<String> uris = ids
                .stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toSet());

        Optional<LocalDateTime> start = eventRepository.getStart(ids);

        Map<Long, Long> views = new HashMap<>();

        if (start.isPresent()) {
            List<ViewStatistic> response = statisticClient
                    .getStats(start.get(), LocalDateTime.now(), uris, true);

            response.forEach(dto -> {
                Long eventId = Long.parseLong(dto.getUri().split("/")[2]);
                views.put(eventId, dto.getHits());
            });
        } else {
            ids.forEach(id -> views.put(id, 0L));
        }
        return views;
    }
}
