package ru.practicum.main_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.model.event.EventEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByInitiatorId(Long userId, Pageable page);

    Optional<EventEntity> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<EventEntity> findByIdAndPublishedOnIsNotNull(Long id);

    List<EventEntity> findAllByIdIn(List<Long> events);

    boolean existsByCategoryId(Long catId);
}