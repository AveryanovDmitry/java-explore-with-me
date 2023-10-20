package ru.practicum.main_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main_service.model.request.Request;
import ru.practicum.main_service.model.request.RequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("select r from Request as r " +
            "join EventEntity as e ON r.event = e.id " +
            "where r.event = :eventId and e.initiator.id = :userId")
    List<Request> findAllByEventWithInitiator(@Param(value = "userId") Long userId,
                                              @Param("eventId") Long eventId);
    List<Request> findAllByStatusAndEventIn(RequestStatus status, Collection<Long> ids);
    Boolean existsByRequesterAndEvent(Long userId, Long eventId);

    List<Request> findAllByRequester(Long userId);

    List<Request> findAllByEvent(Long eventId);

    Optional<Request> findByRequesterAndId(Long userId, Long requestId);
}
