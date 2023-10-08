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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import ua.foxminded.car.microservice.dao.entities.Category;
import ua.foxminded.car.microservice.dao.service.CategoryService;
import ua.foxminded.car.microservice.validation.InfoConstants;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = InfoConstants.CREATE_CATEGORY)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.CREATE_SUCCESS, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "409", description = InfoConstants.CATEGORY_EXISTS, content = @Content) })
    @PostMapping
    public ResponseEntity<Integer> createCategory(@RequestBody Category category) {
        try {
            int categoryId = categoryService.createCategory(category);
            return ResponseEntity.ok(categoryId);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = InfoConstants.DELETE_CATEGORY)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = InfoConstants.DELETE_SUCCESS, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "404", description = InfoConstants.CATEGORY_NOT_FOUND, content = @Content) })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int categoryId) {
        try {
            categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = InfoConstants.UPDATE_CATEGORY)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.UPDATE_SUCCESS, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "404", description = InfoConstants.CATEGORY_NOT_FOUND, content = @Content) })
    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable int categoryId, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategoryById(categoryId, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = InfoConstants.GET_CATEGORY_BY_ID)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.CATEGORY_FOUND, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "404", description = InfoConstants.CATEGORY_NOT_FOUND, content = @Content) })
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable int categoryId) {
        Optional<Category> category = categoryService.findCategoryById(categoryId);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = InfoConstants.LIST_CATEGORIES)
    @ApiResponse(responseCode = "200", description = InfoConstants.CATEGORIES_LISTED, content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) })
    @GetMapping
    public ResponseEntity<Page<Category>> listCategories(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "categoryName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Page<Category> categoryPage = categoryService.listAllCategories(page, size, sortBy, sortOrder);

        return ResponseEntity.ok(categoryPage);
    }

    @Operation(summary = InfoConstants.SEARCH_CATEGORIES)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.CATEGORIES_LISTED, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "404", description = InfoConstants.CATEGORY_NOT_FOUND, content = @Content) })
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
