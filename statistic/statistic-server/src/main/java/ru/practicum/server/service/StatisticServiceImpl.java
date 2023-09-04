package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ViewStatistic;
import ru.practicum.server.mapper.StatisticMapper;
import ru.practicum.server.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository repository;
    private final StatisticMapper mapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ResponseHitDto addEndpointHit(RequestHitDto requestHitDto) {
        return mapper.modelHitToResponseDto(repository.save(mapper.requestDtoToModelHit(requestHitDto)));
    }

    @Override
    public List<ViewStatistic> getStats(String start, String end, List<String> uris, Boolean unique) {
        return repository.getStatistics(
                LocalDateTime.parse(start, FORMATTER),
                LocalDateTime.parse(end, FORMATTER),
                uris, unique).stream().map(mapper::modelViewHitToDtoViewStatistic).collect(Collectors.toList());
    }
}