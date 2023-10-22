package ru.practicum.main_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.model.Comment;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEvent_Id(Long eventId, Pageable pageable);
}