package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    @NotBlank(message = "field app can't be null or empty")
    @Size(max = 255)
    private String app;
    @NotBlank(message = "field uri can't be null or empty")
    @Size(max = 255)
    private String uri;
    @NotBlank(message = "field ip can't be null or empty")
    @Size(max = 255)
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "field timestamp can't be null")
    private LocalDateTime timestamp;
}