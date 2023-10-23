package ru.practicum.main_service.model.comment;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.model.event.EventEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @Column(name = "created_time")
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity author;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;
    @Column(name = "update_Time")
    LocalDateTime updateTime;
}