package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatistic;
import ru.practicum.server.exception.BadRequestException;
import ru.practicum.server.mapper.StatisticMapper;
import ru.practicum.server.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository repository;
    private final StatisticMapper mapper;

    @Override
    public EndpointHitDto addEndpointHit(EndpointHitDto requestHitDto) {
        return mapper.modelHitToResponseDto(repository.save(mapper.requestDtoToModelHit(requestHitDto)));
    }

    @Override
    public List<ViewStatistic> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Date start must be before date end");
        }
        return repository.getStatistics(start, end,
                uris, unique).stream().map(mapper::modelViewHitToDtoViewStatistic).collect(Collectors.toList());
    }
}