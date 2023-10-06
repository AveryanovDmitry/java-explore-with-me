package ru.practicum.main_service.dto.categoryDto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String name;
}
