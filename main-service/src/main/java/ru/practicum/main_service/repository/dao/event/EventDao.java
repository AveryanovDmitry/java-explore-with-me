package ru.practicum.main_service.repository.dao.event;

import ru.practicum.main_service.model.event.EventEntity;
import ru.practicum.main_service.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventDao {

    List<EventEntity> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                String rangeEnd, Integer from, Integer size,
                                LocalDateTime start, LocalDateTime end);
    
    List<EventEntity> getEventsByUsers(List<Long> users, List<EventState> states,
                                       List<Long> categoriesId, String rangeStart, String rangeEnd,
                                       Integer from, Integer size, LocalDateTime start, LocalDateTime end);
}
