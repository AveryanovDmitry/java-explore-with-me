package ru.practicum.server.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModelViewHit {
    private String app;
    private String uri;
    private Long hits;
}
