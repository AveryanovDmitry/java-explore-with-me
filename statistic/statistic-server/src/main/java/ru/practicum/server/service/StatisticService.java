package ru.practicum.server.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    EndpointHitDto addEndpointHit(EndpointHitDto requestHitDto);

    List<ViewStatistic> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}