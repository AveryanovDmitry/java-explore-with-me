package ru.practicum.main_service.service;

import ru.practicum.main_service.dto.eventDto.*;
import ru.practicum.main_service.model.event.EventEntity;
import ru.practicum.main_service.model.event.EventState;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByCurrentUserID(Long userId, Integer from, Integer size);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByCurrentUserIdAndEventId(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto);

    List<EventShortDto> getEventsWithSort(String text, List<Long> categories, Boolean paid, String rangeStart,
                                         String rangeEnd, Boolean onlyAvailable, String sort,
                                         Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminDto updateEventAdminDto);

    List<EventFullDto> getEventsWithParamsByAdmin(List<Long> users, List<EventState> states, List<Long> categoriesId,
                                                  String rangeStart, String rangeEnd, Integer from, Integer size);

    void setView(List<EventEntity> events);
}
