package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.main_service.dto.categoryDto.CategoryDto;
import ru.practicum.main_service.model.CategoryEntity;

@Mapper(componentModel = "Spring")
@Component
public interface CategoryMapper {
    CategoryEntity fromDtoToEntityCategory(CategoryDto categoryDto);

    CategoryDto fromEntityToDtoCategory(CategoryEntity categoryDto);
}
