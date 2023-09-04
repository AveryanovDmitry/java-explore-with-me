package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ViewStatistic;
import ru.practicum.server.service.StatisticService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatisticController {
    private final StatisticService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseHitDto addEndpointHit(@RequestBody RequestHitDto endpointHitDto) {
        log.info("получен запрос на добавление в статистику: {}", endpointHitDto);
        return statisticService.addEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatistic> getStats(@RequestParam String start,
                                        @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        return statisticService.getStats(start, end, uris, unique);
    }
}