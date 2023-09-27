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
import ua.foxminded.car.microservice.entities.Car;
import ua.foxminded.car.microservice.service.CarService;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping
    public ResponseEntity<UUID> createCar(@RequestBody Car car) {
        try {
            UUID carId = carService.createCar(car);
            return ResponseEntity.ok(carId);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carId) {
        try {
            carService.deleteCarById(carId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{carId}")
    public ResponseEntity<Car> updateCar(@PathVariable UUID carId, @RequestBody Car car) {
        try {
            Car updatedCar = carService.updateCarById(carId, car);
            return ResponseEntity.ok(updatedCar);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{carId}/assign-category")
    public ResponseEntity<Void> assignCarToCategory(@PathVariable UUID carId, @RequestParam String categoryName) {
        try {
            carService.assignCarToCategory(carId, categoryName);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{carId}/remove-category")
    public ResponseEntity<Void> removeCarFromCategory(@PathVariable UUID carId, @RequestParam String categoryName) {
        try {
            carService.removeCarFromCategory(carId, categoryName);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{carId}")
    public ResponseEntity<Car> getCar(@PathVariable UUID carId) {
        Optional<Car> car = carService.findCarById(carId);
        return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Car>> listCars(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "make") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Page<Car> carsPage = carService.listAllCars(page, size, sortBy, sortOrder);

        return ResponseEntity.ok(carsPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Car>> searchCars(@RequestParam(required = false) String make,
            @RequestParam(required = false) String model, @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear, @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "make") String sortBy, @RequestParam(defaultValue = "asc") String sortOrder) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);

        Page<Car> searchResult = carService.searchCars(make, model, minYear, maxYear, category, pageRequest);

        return ResponseEntity.ok(searchResult);
    }
}