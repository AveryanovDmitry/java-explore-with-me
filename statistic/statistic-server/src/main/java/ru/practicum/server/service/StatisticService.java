package ru.practicum.server.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatistic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface StatisticService {
    EndpointHitDto addEndpointHit(EndpointHitDto requestHitDto);

    List<ViewStatistic> getStats(LocalDateTime start, LocalDateTime end, Set<String> uris, Boolean unique);
}