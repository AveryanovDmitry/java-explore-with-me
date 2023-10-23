package ru.practicum.main_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main_service.model.comment.Comment;
import ru.practicum.main_service.model.comment.CountComment;

import java.util.Collection;
import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEvent_Id(Long eventId, Pageable pageable);

    @Query("SELECT new ru.practicum.main_service.model.comment.CountComment(comment.event.id, COUNT(comment.id))" +
            "FROM Comment comment " +
            "WHERE comment.event.id IN :eventIds " +
            "GROUP BY comment.event.id")
    List<CountComment> getCountCommentsByIds(@Param("eventIds") Collection<Long> eventIds);
}