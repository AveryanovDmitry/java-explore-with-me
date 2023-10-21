package ru.practicum.main_service.repository.dao.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main_service.model.event.EventEntity;
import ru.practicum.main_service.model.event.EventState;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {
    private final EntityManager entityManager;

    public List<EventEntity> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                       String rangeEnd, Integer from, Integer size,
                                       LocalDateTime start, LocalDateTime end) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);

        Root<EventEntity> root = query.from(EventEntity.class);
        Predicate criteria = builder.conjunction();

        if (text != null) {
            Predicate annotationContain = builder.like(builder.lower(root.get("annotation")),
                    "%" + text.toLowerCase() + "%");
            Predicate descriptionContain = builder.like(builder.lower(root.get("description")),
                    "%" + text.toLowerCase() + "%");
            Predicate containText = builder.or(annotationContain, descriptionContain);

            criteria = builder.and(criteria, containText);
        }

        if (categories != null && categories.size() > 0) {
            Predicate containStates = root.get("category").in(categories);
            criteria = builder.and(criteria, containStates);
        }

        if (paid != null) {
            Predicate isPaid;
            if (paid) {
                isPaid = builder.isTrue(root.get("paid"));
            } else {
                isPaid = builder.isFalse(root.get("paid"));
            }
            criteria = builder.and(criteria, isPaid);
        }

        if (rangeStart != null) {
            Predicate greaterTime = builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start);
            criteria = builder.and(criteria, greaterTime);
        }
        if (rangeEnd != null) {
            Predicate lessTime = builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end);
            criteria = builder.and(criteria, lessTime);
        }

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));

        return entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<EventEntity> getEventsByUsers(List<Long> users, List<EventState> states,
                                              List<Long> categoriesId, String rangeStart, String rangeEnd,
                                              Integer from, Integer size, LocalDateTime start, LocalDateTime end) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);

        Root<EventEntity> root = query.from(EventEntity.class);
        Predicate criteria = builder.conjunction();

        if (categoriesId != null && !categoriesId.isEmpty()) {
            Predicate containCategories = root.get("category").in(categoriesId);
            criteria = builder.and(criteria, containCategories);
        }
        if (users != null && !users.isEmpty()) {
            Predicate containUsers = root.get("initiator").in(users);
            criteria = builder.and(criteria, containUsers);
        }
        if (states != null && !states.isEmpty()) {
            Predicate containStates = root.get("state").in(states);
            criteria = builder.and(criteria, containStates);
        }
        if (rangeStart != null) {
            Predicate greaterTime = builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start);
            criteria = builder.and(criteria, greaterTime);
        }
        if (rangeEnd != null) {
            Predicate lessTime = builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end);
            criteria = builder.and(criteria, lessTime);
        }

        query.select(root).where(criteria);

        return entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }
}
