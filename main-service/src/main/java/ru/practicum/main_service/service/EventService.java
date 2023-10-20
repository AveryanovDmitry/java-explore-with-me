package ru.practicum.main_service.service;

import ru.practicum.main_service.dto.eventDto.*;
import ru.practicum.main_service.dto.eventDto.updateEventDto.UpdateEventDto;
import ru.practicum.main_service.model.event.EventState;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByCurrentUserID(Long userId, Integer from, Integer size);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    List<EventShortDto> getEventsWithSort(String text, List<Long> categories, Boolean paid, String rangeStart,
                                          String rangeEnd, Boolean onlyAvailable, String sort,
                                          Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto updateEvent);


    List<EventFullDto> getEventsWithParamsByAdmin(List<Long> users, List<EventState> states, List<Long> categoriesId,
                                                  String rangeStart, String rangeEnd, Integer from, Integer size);
}
