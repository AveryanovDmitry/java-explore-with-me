package ru.practicum.server.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ModelViewHit {
    private String app;
    private String uri;
    private Long hits;
}
