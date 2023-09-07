package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RequestHitDto {
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