package ru.practicum.main_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.dto.categoryDto.CategoryDto;
import ru.practicum.main_service.exeptions.ConflictParametersException;
import ru.practicum.main_service.exeptions.NotFoundException;
import ru.practicum.main_service.exeptions.AlreadyCreatedException;
import ru.practicum.main_service.mapper.CategoryMapper;
import ru.practicum.main_service.model.CategoryEntity;
import ru.practicum.main_service.repository.CategoryRepository;
import ru.practicum.main_service.repository.EventRepository;
import ru.practicum.main_service.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new AlreadyCreatedException("This category has already been added");
        }
        CategoryEntity category = categoryRepository.save(categoryMapper.fromDtoToEntityCategory(categoryDto));
        log.info("Сохранена новая категория {} под id {}", category.getName(), category.getId());
        return categoryMapper.fromEntityToDtoCategory(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictParametersException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
        log.info("Удалена категория по id {}", catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        CategoryEntity category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category doesn't exist"));
        if (!categoryDto.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(categoryDto.getName())) {
                throw new AlreadyCreatedException(
                        String.format("Can't update category because name: %s already used by another category",
                                categoryDto.getName()));
            }
            category.setName(categoryDto.getName());
        }
        return categoryMapper.fromEntityToDtoCategory(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page)
                .stream()
                .map(categoryMapper::fromEntityToDtoCategory)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        return categoryMapper.fromEntityToDtoCategory(
                categoryRepository.findById(catId)
                        .orElseThrow(()-> new NotFoundException("Категория с таким id не найдена")));
    }
}
