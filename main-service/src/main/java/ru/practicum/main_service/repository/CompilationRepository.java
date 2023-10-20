package ru.practicum.main_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_service.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

}