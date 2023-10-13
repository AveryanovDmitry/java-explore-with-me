package ru.practicum.main_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.model.event.EventEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByInitiatorId(Long userId, Pageable page);

    Optional<EventEntity> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<EventEntity> findByIdAndPublishedOnIsNotNull(Long id);

    List<EventEntity> findAllByIdIn(Set<Long> events);

    boolean existsByCategoryId(Long catId);

//    @Query("SELECT MIN(e.publishedOn) FROM EventEntity e WHERE e.id IN :eventsId")
//    Optional<LocalDateTime> getStart(@Param("eventsId") Collection<Long> eventsId);
}
