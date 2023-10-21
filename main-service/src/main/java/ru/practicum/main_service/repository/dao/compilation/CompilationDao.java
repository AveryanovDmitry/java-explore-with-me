package ru.practicum.main_service.repository.dao.compilation;

import ru.practicum.main_service.model.Compilation;

import java.util.List;

public interface CompilationDao {
    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size);
}
