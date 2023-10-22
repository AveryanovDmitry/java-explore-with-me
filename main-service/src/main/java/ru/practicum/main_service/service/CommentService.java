package ru.practicum.main_service.service;

import ru.practicum.main_service.dto.comment.CommentDto;
import ru.practicum.main_service.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto updateCommentByUser(NewCommentDto newCommentDto, Long userId, Long commentId);

    CommentDto getCommentsByIdByUser(Long userId, Long commentId);

    void deleteCommentByUser(Long userId, Long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    CommentDto getCommentsByIdByAdmin(Long commentId);

    void deleteCommentByAdmin(Long commentId);
}