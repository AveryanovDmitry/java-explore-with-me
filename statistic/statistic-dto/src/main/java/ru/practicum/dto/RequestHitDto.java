package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RequestHitDto {
    @NotNull(message = "field app can't be null")
    @Size(max = 255)
    private String app;
    @NotNull(message = "field uri can't be null")
    @Size(max = 255)
    private String uri;
    @NotNull(message = "field ip can't be null")
    @Size(max = 255)
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}