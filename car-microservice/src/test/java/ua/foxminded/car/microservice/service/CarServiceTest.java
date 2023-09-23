package ua.foxminded.car.microservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
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
import ua.foxminded.car.microservice.entities.Car;
import ua.foxminded.university.dao.validation.InfoConstants;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { CarService.class, }))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test-container")
@Sql(scripts = { "/drop_data.sql", "/init_tables.sql",
        "/insert_test_data.sql", }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CarServiceTest {

    @Autowired
    private CarService carService;

    @ParameterizedTest
    @CsvSource({ "VW", "Hyundai", "Scoda" })
    void testDeleteCarById_ShouldReturnCarId(String make) {
        List<Car> cars = carService.findCarsByMake(make);

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
    @CsvSource({ "VW, Passat, 2006", "Hyundai, Tucson, 2007", "Audi, A5, 2011", "Scoda, Superb, 2013" })
    void testUpdateCarById_ShouldReturnUpdatedCar(String make, String model, int year) {
        List<Car> cars = carService.findCarsByMake(make);

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
    @CsvSource({ "VW, Sedan", "Hyundai, Hatchback", "Audi, SUV", "Scoda, Sedan" })
    void testAssignCarToCategory_ShouldReturnAssignedQuantity(String make, String categoryName) {
        List<Car> cars = carService.findCarsByMake(make);

        for (Car car : cars) {
            Assertions.assertEquals(1, carService.assignCarToCategory(car.getObjectId(), categoryName));
        }
    }

    @ParameterizedTest
    @CsvSource({ "VW, Sedan", "Hyundai, Hatchback", "Audi, SUV", "Scoda, Sedan" })
    void testRemoveCarFromCategory_ShouldReturnRemovedQuantity(String make, String categoryName) {
        List<Car> cars = carService.findCarsByMake(make);

        for (Car car : cars) {
            Assertions.assertEquals(1, carService.removeCarFromCategory(car.getObjectId(), categoryName));
        }
    }
}
