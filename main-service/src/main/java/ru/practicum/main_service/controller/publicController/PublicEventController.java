package ru.practicum.main_service.controller.publicController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.eventDto.EventFullDto;
import ru.practicum.main_service.dto.eventDto.EventShortDto;
import ru.practicum.main_service.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsWithParamsByUser(@RequestParam(name = "text", required = false) String text,
                                                         @RequestParam(name = "categories", required = false) List<Long> categories,
                                                         @RequestParam(name = "paid", required = false) Boolean paid,
                                                         @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                         @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                         @RequestParam(name = "onlyAvailable", required = false) boolean onlyAvailable,
                                                         @RequestParam(name = "sort", required = false) String sort,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                         HttpServletRequest request) {
        return eventService.getEventsWithSort(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получен запрос на получение события по id {}", id);
        return eventService.getEvent(id, request);
    }
}