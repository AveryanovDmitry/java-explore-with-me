package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatistic;
import ru.practicum.server.service.StatisticService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatisticController {
    private final StatisticService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto addEndpointHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("получен запрос на добавление в статистику: {}", endpointHitDto);
        return statisticService.addEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatistic> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam(required = false) Set<String> uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("запрос на получение статистики с {} по {} url {}", start, end, uris);
        return statisticService.getStats(start, end, uris, unique);
    }
}