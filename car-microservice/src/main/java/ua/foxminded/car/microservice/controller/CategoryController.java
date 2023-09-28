package ua.foxminded.car.microservice.controller;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import ua.foxminded.car.microservice.entities.Category;
import ua.foxminded.car.microservice.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Integer> createCar(@RequestBody Category category) {
        try {
            int categoryId = categoryService.createCategory(category);
            return ResponseEntity.ok(categoryId);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int categoryId) {
        try {
            categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCar(@PathVariable int categoryId, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategoryById(categoryId, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable int categoryId) {
        Optional<Category> category = categoryService.findCategoryById(categoryId);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Category>> listCategories(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "categoryName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Page<Category> categoryPage = categoryService.listAllCategories(page, size, sortBy, sortOrder);

        return ResponseEntity.ok(categoryPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Category>> searchCategories(@RequestParam(required = false) String categoryName,
            @RequestParam(required = false) UUID carId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "categoryName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
            Page<Category> searchResult = categoryService.searchCategories(categoryName, carId, pageRequest);
            return ResponseEntity.ok(searchResult);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
