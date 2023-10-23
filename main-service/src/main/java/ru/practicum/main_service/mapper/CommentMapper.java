package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_service.dto.comment.CommentDto;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.model.comment.Comment;
import ru.practicum.main_service.model.event.EventEntity;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "event.id", target = "eventId")
    CommentDto toCommentDto(Comment comment);

    List<CommentDto> toCommentDtos(List<Comment> comment);

    default Comment createCommentForSave(UserEntity author, EventEntity event, LocalDateTime created, String text) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreated(created);
        comment.setText(text);
        return comment;
    }

}