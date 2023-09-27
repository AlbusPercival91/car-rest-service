package ua.foxminded.car.microservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.car.microservice.entities.Category;
import ua.foxminded.car.microservice.repository.CategoryRepository;
import ua.foxminded.university.dao.validation.InfoConstants;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public int createCategory(Category category) {
        if (categoryRepository.findById(category.getCategoryId()).isPresent()) {
            log.info(InfoConstants.CATEGORY_EXISTS);
            throw new EntityExistsException(InfoConstants.CATEGORY_EXISTS);
        }
        Category newCategory = categoryRepository.save(category);
        log.info(InfoConstants.CREATE_SUCCESS);
        return newCategory.getCategoryId();
    }

    public int deleteCategoryById(int categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            log.info(InfoConstants.CATEGORY_NOT_FOUND);
            throw new EntityNotFoundException(InfoConstants.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(categoryId);
        log.info(InfoConstants.DELETE_SUCCESS);
        return categoryId;
    }

    public Category updateCategoryById(int categoryId, Category targetCategory) {
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.warn(InfoConstants.CATEGORY_NOT_FOUND);
            return new EntityNotFoundException(InfoConstants.CATEGORY_NOT_FOUND);
        });

        BeanUtils.copyProperties(targetCategory, existingCategory, "categoryId");
        return categoryRepository.save(existingCategory);
    }

    public Optional<Category> findCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Optional<Category> findCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    public Page<Category> listAllCategories(int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        return categoryRepository.findAll(pageRequest);
    }

    public List<Category> getCarCategories(UUID carId) {
        return categoryRepository.getCarCategories(carId);
    }
}
