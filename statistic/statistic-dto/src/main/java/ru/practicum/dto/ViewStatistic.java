package ru.practicum.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Setter
public class ViewStatistic {
    private String app;
    private String uri;
    private Long hits;
}