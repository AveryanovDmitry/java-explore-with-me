package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main_service.dto.compilation.CompilationDto;
import ru.practicum.main_service.model.Compilation;


import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto mapToCompilationDto(Compilation compilation);

    List<CompilationDto> mapToListCompilationDto(List<Compilation> compilations);
}