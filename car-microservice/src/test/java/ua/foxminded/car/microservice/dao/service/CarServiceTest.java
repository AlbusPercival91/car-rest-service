package ua.foxminded.car.microservice.dao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.car.microservice.dao.entities.Car;
import ua.foxminded.car.microservice.dao.entities.Category;
import ua.foxminded.car.microservice.validation.InfoConstants;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { CarService.class,
        CategoryService.class }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
        "/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CarServiceTest {

    @Autowired
    private CarService carService;

    @Autowired
    private CategoryService categoryService;

    @ParameterizedTest
    @CsvSource({ "VW, 0, 10, make, asc", "Hyundai, 0, 10, make, asc", "Scoda, 0, 10, make, asc" })
    void testDeleteCarById_ShouldReturnCarId(String make, int page, int size, String sortBy, String sortOrder) {
        Page<Car> cars = carService.findCarsByMake(make, page, size, sortBy, sortOrder);

        for (Car car : cars) {
            Assertions.assertEquals(car.getObjectId(), carService.deleteCarById(car.getObjectId()));
        }
    }

    @Test
    void testDeleteCarById_WhenCarNotFound_ShouldThrowEntityNotFoundException() {
        Car car = new Car();
        car.setObjectId(UUID.randomUUID());

        Exception entityNotFoundException = assertThrows(Exception.class,
                () -> carService.deleteCarById(car.getObjectId()));
        Assertions.assertEquals(InfoConstants.CAR_NOT_FOUND, entityNotFoundException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "VW, Passat, 2006, 0, 10, make, asc", "Hyundai, Tucson, 2007, 0, 10, make, asc",
            "Audi, A5, 2011, 0, 10, make, asc", "Scoda, Superb, 2013, 0, 10, make, asc" })
    void testUpdateCarById_ShouldReturnUpdatedCar(String make, String model, int year, int page, int size,
            String sortBy, String sortOrder) {
        Page<Car> cars = carService.findCarsByMake(make, page, size, sortBy, sortOrder);

        Car expectedCar = new Car(make, model, year);

        for (Car car : cars) {
            Assertions.assertEquals(car.getObjectId(),
                    carService.updateCarById(car.getObjectId(), expectedCar).getObjectId());
            Assertions.assertEquals(expectedCar.getModel(),
                    carService.updateCarById(car.getObjectId(), expectedCar).getModel());
            Assertions.assertEquals(expectedCar.getYear(),
                    carService.updateCarById(car.getObjectId(), expectedCar).getYear());
        }
    }

    @Test
    void testUpdateCarById_WhenCarNotFound_ShouldThrowEntityNotFoundException() {
        Car car = new Car();
        car.setObjectId(UUID.randomUUID());
        Car expectedCar = new Car();

        Exception entityNotFoundException = assertThrows(Exception.class,
                () -> carService.updateCarById(car.getObjectId(), expectedCar));
        Assertions.assertEquals(InfoConstants.CAR_NOT_FOUND, entityNotFoundException.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "VW, testCategory1, 0, 10, make, asc", "Hyundai, testCategory2, 0, 10, make, asc" })
    void testAssignCarToCategory_ShouldReturnAssignedQuantity(String make, String categoryName, int page, int size,
            String sortBy, String sortOrder) {
        Page<Car> cars = carService.findCarsByMake(make, page, size, sortBy, sortOrder);
        categoryService.createCategory(new Category(categoryName));

        for (Car car : cars) {
            Assertions.assertEquals(1, carService.assignCarToCategory(car.getObjectId(), categoryName));
        }
    }

    @ParameterizedTest
    @CsvSource({ "VW, testCategory, 0, 10, make, asc", "Hyundai, testCategory, 0, 10, make, asc" })
    void testAssignCarToCategory_WhenCarAlreadyAssigned_ShouldThrowIllegalStateException(String make,
            String categoryName, int page, int size, String sortBy, String sortOrder) {
        Page<Car> cars = carService.findCarsByMake(make, page, size, sortBy, sortOrder);
        categoryService.createCategory(new Category(categoryName));

        for (Car car : cars) {
            carService.assignCarToCategory(car.getObjectId(), categoryName);

            Exception illegalStateException = assertThrows(Exception.class,
                    () -> carService.assignCarToCategory(car.getObjectId(), categoryName));
            Assertions.assertEquals(InfoConstants.CAR_BELONGS_TO_CATEGORY, illegalStateException.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource({ "VW, testCategory1, 0, 10, make, asc", "Hyundai, testCategory2, 0, 10, make, asc" })
    void testRemoveCarFromCategory_ShouldReturnRemovedQuantity(String make, String categoryName, int page, int size,
            String sortBy, String sortOrder) {
        Page<Car> cars = carService.findCarsByMake(make, page, size, sortBy, sortOrder);
        categoryService.createCategory(new Category(categoryName));

        for (Car car : cars) {
            carService.assignCarToCategory(car.getObjectId(), categoryName);
            Assertions.assertEquals(1, carService.removeCarFromCategory(car.getObjectId(), categoryName));
        }
    }

    @ParameterizedTest
    @CsvSource({ "VW, testCategory1, 0, 10, make, asc", "Hyundai, testCategory2, 0, 10, make, asc" })
    void testRemoveCarFromCategory_WhenCarIsNotAssigned_ShouldThrowIllegalStateException(String make,
            String categoryName, int page, int size, String sortBy, String sortOrder) {
        Page<Car> cars = carService.findCarsByMake(make, page, size, sortBy, sortOrder);

        for (Car car : cars) {
            Exception illegalStateException = assertThrows(Exception.class,
                    () -> carService.removeCarFromCategory(car.getObjectId(), categoryName));
            Assertions.assertEquals(InfoConstants.CAR_NOT_BELONGS_TO_CATEGORY, illegalStateException.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource({ "Sedan, 0, 10, make, asc", "Hatchback, 0, 10, make, asc", "SUV, 0, 10, make, asc" })
    void testFindCarsByCategory_ShouldReturnNotEmptyPage(String categoryName, int page, int size, String sortBy,
            String sortOrder) {
        Assertions.assertTrue(!carService.findCarsByCategory(categoryName, page, size, sortBy, sortOrder).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({ "testCategory, 0, 10, make, asc" })
    void testFindCarsByCategory_WhenCategoryNotExists_ShouldReturnEmptyPage(String categoryName, int page, int size,
            String sortBy, String sortOrder) {
        Assertions.assertTrue(carService.findCarsByCategory(categoryName, page, size, sortBy, sortOrder).isEmpty());
    }
}
