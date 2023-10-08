package ru.practicum.server.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class ModelViewHit {
    private String app;
    private String uri;
    private Long hits;
}
