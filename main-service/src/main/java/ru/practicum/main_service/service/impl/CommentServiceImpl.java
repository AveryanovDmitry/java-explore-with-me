package ru.practicum.main_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.dto.comment.CommentDto;
import ru.practicum.main_service.dto.comment.NewCommentDto;
import ru.practicum.main_service.exeptions.ConflictParametersException;
import ru.practicum.main_service.exeptions.NotFoundException;
import ru.practicum.main_service.mapper.CommentMapper;
import ru.practicum.main_service.model.comment.Comment;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.model.event.EventEntity;
import ru.practicum.main_service.repository.CommentsRepository;
import ru.practicum.main_service.repository.EventRepository;
import ru.practicum.main_service.repository.UserRepository;
import ru.practicum.main_service.service.CommentService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentsRepository commentsRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Can't create comment, user with id=%s doesn't exist", userId)));
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Can't create comment, event with id=%s doesn't exist", eventId)));

        Comment comment = commentMapper.createCommentForSave(user, event, LocalDateTime.now(), newCommentDto.getText());
        return commentMapper.toCommentDto(commentsRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Can't receive comment by id, event with id=%s doesn't exist", eventId)));
        Pageable page = PageRequest.of(from / size, size);
        List<Comment> eventComments = commentsRepository.findAllByEvent_Id(eventId, page);
        log.debug("Get comment`s list of event with ID = {}", eventId);
        return commentMapper.toCommentDtos(eventComments);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByUser(NewCommentDto newCommentDto, Long userId, Long commentId) {
        Comment oldComment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Can't update comment, comment doesn't exist"));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Can't delete comment, if user doesn't exist");
        }
        if (!oldComment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Can't delete comment, if his owner another user");
        }
        if (oldComment.getCreated().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new ConflictParametersException("Can't update comment, the time for a possible update has expired");
        }
        oldComment.setText(newCommentDto.getText());
        oldComment.setUpdateTime(LocalDateTime.now());
        Comment savedComment = commentsRepository.save(oldComment);
        log.debug("Comment with ID = {} was update", commentId);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public CommentDto getCommentsByIdByUser(Long userId, Long commentId) {
        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Can't get comment"));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Can't get comment, if user doesn't exist");
        }

        if (!userId.equals(comment.getAuthor().getId())) {
            throw new NotFoundException("Can't get comment created by another user");
        }
        log.debug("Get comment with ID = {}", commentId);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        "Can't delete comment, if his owner another user or user/comment doesn't exist"));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Can't delete comment, if user doesn't exist");
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Can't delete comment, if his owner another user");
        }
        log.debug("Comment with ID = {} was delete", commentId);
        commentsRepository.delete(comment);
    }

    @Override
    public CommentDto getCommentsByIdByAdmin(Long commentId) {
        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Can't receive comment by id, the comment with id=%s doesn't exist", commentId)));
        log.debug("Comment with ID = {} was found", commentId);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        commentsRepository.deleteById(commentId);
        log.debug("Comment with ID = {} was delete", commentId);
    }
}