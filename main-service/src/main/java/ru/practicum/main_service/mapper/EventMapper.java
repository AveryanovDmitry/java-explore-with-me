package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_service.dto.eventDto.EventFullDto;
import ru.practicum.main_service.dto.eventDto.EventShortDto;
import ru.practicum.main_service.dto.eventDto.NewEventDto;
import ru.practicum.main_service.model.event.EventEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventFullDto fromEntityToEventFullDto(EventEntity event);

    @Mapping(source = "category", target = "category.id")
    EventEntity fromNewEventToEventEntity(NewEventDto newEventDto);

    List<EventShortDto> toEventShortDtoList(List<EventEntity> events);
}