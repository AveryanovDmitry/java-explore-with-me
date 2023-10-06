package ru.practicum.main_service.service;

import ru.practicum.main_service.dto.requestDto.RequestDto;
import ru.practicum.main_service.dto.requestDto.RequestStatusUpdateDto;
import ru.practicum.main_service.dto.requestDto.RequestStatusUpdateResult;

import java.util.List;

public interface RequestService {
    RequestDto createRequestByInitiatorId(Long initiatorId, Long eventId);

    List<RequestDto> getCurrentUserRequests(Long userId);

    RequestDto cancelRequests(Long userId, Long requestId);

    List<RequestDto> getRequestsByInitiatorIdAndEventId(Long userId, Long eventId);

    RequestStatusUpdateResult updateRequests(Long userId, Long eventId, RequestStatusUpdateDto requestStatusUpdateDto);
}
