package ua.foxminded.car.microservice.dao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import ua.foxminded.car.microservice.dao.entities.Car;
import ua.foxminded.car.microservice.dao.entities.Category;
import ua.foxminded.car.microservice.dao.service.CarService;
import ua.foxminded.car.microservice.dao.service.CategoryService;
import ua.foxminded.car.microservice.validation.InfoConstants;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        CategoryService.class, CarService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
        "/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CarService carservice;

    @ParameterizedTest
    @CsvSource({ "1", "2", "3" })
    void testDeleteCategoryById_ShouldReturnCategoryId(int categoryId) {
        Optional<Category> category = categoryService.findCategoryById(categoryId);

        Assertions.assertEquals(category.get().getCategoryId(),
                categoryService.deleteCategoryById(category.get().getCategoryId()));
    }

    @Test
    void testDeleteCategoryById_WhenCategoryNotFound_ShouldThrowEntityNotFoundException() {
        int categoryId = -1;

        Exception entityNotFoundException = assertThrows(Exception.class,
                () -> categoryService.deleteCategoryById(categoryId));
        Assertions.assertEquals(InfoConstants.CATEGORY_NOT_FOUND, entityNotFoundException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "1", "2", "3" })
    void testUpdateCategoryById_ShouldReturnUpdatedCategory(int categoryId) {
        Category expectedCategory = new Category("Pickup");

        Assertions.assertEquals(categoryId,
                categoryService.updateCategoryById(categoryId, expectedCategory).getCategoryId());
        Assertions.assertEquals(expectedCategory.getCategoryName(),
                categoryService.updateCategoryById(categoryId, expectedCategory).getCategoryName());

    }

    @ParameterizedTest
    @CsvSource({ "-1", "-2", "-3" })
    void testUpdateCategoryById_WhenCategoryNotFound_ShouldThrowEntityNotFoundException(int categoryId) {
        Category expectedCategory = new Category("Pickup");

        Exception entityNotFoundException = assertThrows(Exception.class,
                () -> categoryService.updateCategoryById(categoryId, expectedCategory));
        Assertions.assertEquals(InfoConstants.CATEGORY_NOT_FOUND, entityNotFoundException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "Dodge, Neon, 2003, 1, 0, 10, categoryName, asc", "Nissan, Skyline, 1999, 2, 0, 10, categoryName, asc",
            "Opel, Omega, 1997, 3, 0, 10, categoryName, asc" })
    void testGetCarCategories_ShouldReturnCarCategories(String make, String model, int year, int categoryId, int page,
            int size, String sortBy, String sortOrder) {
        Optional<Category> category = categoryService.findCategoryById(categoryId);
        Car car = new Car(make, model, year);
        car.setObjectId(UUID.randomUUID());

        carservice.createCar(car);
        carservice.assignCarToCategory(car.getObjectId(), category.get().getCategoryName());

        Assertions.assertEquals(true, categoryService.getCarCategories(car.getObjectId(), page, size, sortBy, sortOrder)
                .get().findAny().equals(category));
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 0, 10, categoryName, asc", "2, 1, 0, 10, categoryName, asc", "3, 1, 0, 10, categoryName, asc" })
    void testGetCarCategories_WhenCarNotFound_ShouldThrowEntityNotFoundException(int categoryId, int page, int size,
            String sortBy, String sortOrder) {
        Optional<Category> category = categoryService.findCategoryById(categoryId);

        Exception entityNotFoundException = assertThrows(Exception.class, () -> categoryService
                .getCarCategories(UUID.randomUUID(), page, size, sortBy, sortOrder).get().findAny().equals(category));
        Assertions.assertEquals(InfoConstants.CAR_NOT_FOUND, entityNotFoundException.getMessage());
    }
}
