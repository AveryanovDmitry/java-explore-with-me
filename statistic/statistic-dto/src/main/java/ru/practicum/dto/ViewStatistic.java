package ru.practicum.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ViewStatistic {
    private String app;
    private String uri;
    private Long hits;
}