package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ViewStatistic;
import ru.practicum.server.model.ModelHit;
import ru.practicum.server.model.ModelViewHit;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    ModelHit requestDtoToModelHit(RequestHitDto requestHitDto);

    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    ResponseHitDto modelHitToResponseDto(ModelHit requestHitDto);

    ViewStatistic modelViewHitToDtoViewStatistic(ModelViewHit endpointHit);
}