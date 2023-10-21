package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatistic;
import ru.practicum.server.exception.BadRequestException;
import ru.practicum.server.mapper.StatisticMapper;
import ru.practicum.server.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository repository;
    private final StatisticMapper mapper;

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public EndpointHitDto addEndpointHit(EndpointHitDto requestHitDto) {
        return mapper.modelHitToResponseDto(repository.save(mapper.requestDtoToModelHit(requestHitDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatistic> getStats(@RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
                                        Set<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Date start must be before date end");
        }

        return repository.getStatistics(start, end,
                uris, unique).stream().map(mapper::modelViewHitToDtoViewStatistic).collect(Collectors.toList());
    }
}