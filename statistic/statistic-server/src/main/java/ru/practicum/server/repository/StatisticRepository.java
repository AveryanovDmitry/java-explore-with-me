package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.ModelHit;
import ru.practicum.server.model.ModelViewHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<ModelHit, Long> {
    @Query("SELECT new ru.practicum.server.model.ModelViewHit(" +
            "   entityHit.app, " +
            "   entityHit.uri, " +
            "   CASE WHEN :unique = true " +
            "       THEN count(DISTINCT (entityHit.ip)) " +
            "       else count(entityHit.ip) end)" +
            "FROM ModelHit entityHit " +
            "WHERE entityHit.timestamp BETWEEN :start AND :end" +
            "   AND (coalesce(:uris, null) IS NULL OR entityHit.uri IN :uris) " +
            "GROUP BY entityHit.app, entityHit.uri " +
            "ORDER BY 3 desc")
    List<ModelViewHit> getStatistics(LocalDateTime start,
                                     LocalDateTime end,
                                     List<String> uris,
                                     Boolean unique);
}