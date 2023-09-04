package ru.practicum.server.service;

import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ViewStatistic;

import java.util.List;

public interface StatisticService {
    ResponseHitDto addEndpointHit(RequestHitDto requestHitDto);

    List<ViewStatistic> getStats(String start, String end, List<String> uris, Boolean unique);
}