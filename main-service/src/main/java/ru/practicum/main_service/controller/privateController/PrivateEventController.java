package ru.practicum.main_service.controller.privateController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.eventDto.EventFullDto;
import ru.practicum.main_service.dto.eventDto.EventShortDto;
import ru.practicum.main_service.dto.eventDto.NewEventDto;
import ru.practicum.main_service.dto.eventDto.UpdateEventUserDto;
import ru.practicum.main_service.dto.requestDto.RequestDto;
import ru.practicum.main_service.dto.requestDto.RequestStatusUpdateDto;
import ru.practicum.main_service.dto.requestDto.RequestStatusUpdateResult;
import ru.practicum.main_service.service.EventService;
import ru.practicum.main_service.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventController {
    private final EventService eventService;

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Получил запрос на создание события");
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getAllEventsByUser(@PathVariable Long userId,
                                                  @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Получил запрос на получение событий текущего пользователя");
        return eventService.getEventsByCurrentUserID(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdAndUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получил запрос на получение события текущего пользователя по id события");
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventUserDto updateEventUserDto) {
        log.info("Получил запрос на обновления события текущего пользователя по id события");
        return eventService.updateEventByCurrentUserIdAndEventId(userId, eventId, updateEventUserDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByOwnerOfEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestsByInitiatorIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResult updateRequests(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @RequestBody RequestStatusUpdateDto requestStatusUpdateDto) {
        return requestService.updateRequests(userId, eventId, requestStatusUpdateDto);
    }
}
