package ru.practicum.main_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.dto.requestDto.RequestDto;
import ru.practicum.main_service.dto.requestDto.RequestStatusUpdateDto;
import ru.practicum.main_service.dto.requestDto.RequestStatusUpdateResult;
import ru.practicum.main_service.exeptions.ConflictParametersException;
import ru.practicum.main_service.exeptions.NotFoundException;
import ru.practicum.main_service.exeptions.AlreadyCreatedException;
import ru.practicum.main_service.mapper.RequestMapper;
import ru.practicum.main_service.model.event.EventEntity;
import ru.practicum.main_service.model.request.Request;
import ru.practicum.main_service.model.request.RequestStatus;
import ru.practicum.main_service.repository.EventRepository;
import ru.practicum.main_service.repository.RequestRepository;
import ru.practicum.main_service.repository.UserRepository;
import ru.practicum.main_service.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;

    @Override
    public RequestDto createRequestByInitiatorId(Long initiatorId, Long eventId) {
        log.info("Добавляем запрос от инициатора события");
        if (requestRepository.existsByRequesterAndEvent(initiatorId, eventId)) {
            throw new AlreadyCreatedException("Request already exists");
        }
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event doesnt exist"));

        if (event.getInitiator().getId().equals(initiatorId)) {
            throw new ConflictParametersException("Can't create request by initiator");
        }
        if (event.getPublishedOn() == null) {
            throw new ConflictParametersException("Event is not published yet");
        }

        List<Request> requests = requestRepository.findAllByEvent(eventId);
        if (!event.getRequestModeration() && requests.size() >= event.getParticipantLimit()) {
            throw new ConflictParametersException("Member limit exceeded ");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(eventId);
        request.setRequester(initiatorId);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getCurrentUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));
        return requestMapper.toRequestDtoList(requestRepository.findAllByRequester(userId));
    }

    @Override
    public RequestDto cancelRequests(Long userId, Long requestId) {
        Request request = requestRepository
                .findByRequesterAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%s was not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getRequestsByInitiatorIdAndEventId(Long userId, Long eventId) {
        log.info("Получаем запросы по инициатору события и его id");
        return requestMapper.toRequestDtoList(requestRepository.findAllByEventWithInitiator(userId, eventId));
    }

    @Override
    public RequestStatusUpdateResult updateRequests(Long userId, Long eventId, RequestStatusUpdateDto requestStatusUpdateDto) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event doesn't exist"));
        RequestStatusUpdateResult result = new RequestStatusUpdateResult();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        List<Request> requests = requestRepository.findAllByEventWithInitiator(userId, eventId);
        List<Request> requestsForUpdate = requests.stream()
                .filter(x -> requestStatusUpdateDto.getRequestIds().contains(x.getId())).collect(Collectors.toList());

        if (requestsForUpdate.stream().anyMatch(x -> x.getStatus().equals(RequestStatus.CONFIRMED)
                && requestStatusUpdateDto.getStatus().equals(RequestStatus.REJECTED))) {
            throw new ConflictParametersException("request already confirmed");
        }

        Long confirmedRequest = getConfirmedRequests(Collections.singleton(event.getId()))
                .getOrDefault(event.getId(), 0L);
        if (confirmedRequest + requestsForUpdate.size() > event.getParticipantLimit()
                && requestStatusUpdateDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConflictParametersException("exceeding the limit of participants");
        }

        for (Request request : requestsForUpdate) {
            request.setStatus(RequestStatus.valueOf(requestStatusUpdateDto.getStatus().toString()));
        }

        requestRepository.saveAll(requestsForUpdate);
        eventRepository.save(event);

        if (requestStatusUpdateDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            result.setConfirmedRequests(requestMapper.toRequestDtoList(requestsForUpdate));
        }

        if (requestStatusUpdateDto.getStatus().equals(RequestStatus.REJECTED)) {
            result.setRejectedRequests(requestMapper.toRequestDtoList(requestsForUpdate));
        }

        return result;
    }

    public Map<Long, Long> getConfirmedRequests(Collection<Long> ids) {
        List<Request> confirmedRequests = requestRepository.findAllByStatusAndEventIn(RequestStatus.CONFIRMED, ids);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(Request::getEvent))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, requestList -> (long) requestList.getValue().size()));
    }
}
