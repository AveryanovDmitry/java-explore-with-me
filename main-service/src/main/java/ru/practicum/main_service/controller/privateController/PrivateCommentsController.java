package ru.practicum.main_service.controller.privateController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.comment.CommentDto;
import ru.practicum.main_service.dto.comment.NewCommentDto;
import ru.practicum.main_service.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users/{userId}/comments")
@Validated
@RequiredArgsConstructor
public class PrivateCommentsController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId) {
        return commentService.createComment(newCommentDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId) {
        return commentService.updateCommentByUser(newCommentDto, userId, commentId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable(value = "userId") Long userId,
                                     @PathVariable(value = "commentId") Long commentId) {
        return commentService.getCommentsByIdByUser(userId, commentId);
    }


    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }
}
