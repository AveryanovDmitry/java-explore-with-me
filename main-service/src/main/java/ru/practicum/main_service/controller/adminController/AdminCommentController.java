package ru.practicum.main_service.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.comment.CommentDto;
import ru.practicum.main_service.service.CommentService;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable(value = "commentId") Long commentId) {
        return commentService.getCommentsByIdByAdmin(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}