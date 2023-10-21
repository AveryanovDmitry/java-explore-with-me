package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main_service.dto.location.LocationDto;
import ru.practicum.main_service.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toLocation(LocationDto locationDtoCoordinates);

    LocationDto toLocationDtoCoordinates(Location location);
}
