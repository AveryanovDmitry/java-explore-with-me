package ru.practicum.main_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class LocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    private Float lat;

    @Min(-180)
    @Max(180)
    @NotNull
    private Float lon;
}
