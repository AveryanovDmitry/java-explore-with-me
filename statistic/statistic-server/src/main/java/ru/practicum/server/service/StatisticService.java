package ru.practicum.server.service;

import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ViewStatistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    ResponseHitDto addEndpointHit(RequestHitDto requestHitDto);

    List<ViewStatistic> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}