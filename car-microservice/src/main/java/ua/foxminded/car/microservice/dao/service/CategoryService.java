package ua.foxminded.car.microservice.dao.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.car.microservice.dao.entities.Category;
import ua.foxminded.car.microservice.dao.repository.CarRepository;
import ua.foxminded.car.microservice.dao.repository.CategoryRepository;
import ua.foxminded.car.microservice.validation.InfoConstants;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;

    public int createCategory(Category category) {
        if (categoryRepository.findById(category.getCategoryId()).isPresent()) {
            log.warn(InfoConstants.CATEGORY_EXISTS);
            throw new EntityExistsException(InfoConstants.CATEGORY_EXISTS);
        }
        Category newCategory = categoryRepository.save(category);
        log.info(InfoConstants.CREATE_SUCCESS);
        return newCategory.getCategoryId();
    }

    public int deleteCategoryById(int categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            log.warn(InfoConstants.CATEGORY_NOT_FOUND);
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
        log.info(InfoConstants.UPDATE_SUCCESS);
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

    public Page<Category> getCarCategories(UUID carId, int page, int size, String sortBy, String sortOrder) {
        if (carRepository.findById(carId).isPresent()) {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
            return categoryRepository.getCarCategories(carId, pageRequest);
        }
        log.warn(InfoConstants.CAR_NOT_FOUND);
        throw new EntityNotFoundException(InfoConstants.CAR_NOT_FOUND);
    }

    public Page<Category> searchCategories(String categoryName, UUID carId, Pageable pageable) {
        if (categoryName != null && !categoryName.isEmpty()) {
            return findCategoryByName(categoryName)
                    .map(category -> new PageImpl<>(Collections.singletonList(category), pageable, 1))
                    .orElseGet(() -> new PageImpl<>(Collections.emptyList(), pageable, 0));
        } else if (carId != null) {
            return categoryRepository.getCarCategories(carId, pageable);
        } else {
            return categoryRepository.findAll(pageable);
        }
    }

}
