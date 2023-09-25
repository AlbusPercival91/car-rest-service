package ua.foxminded.car.microservice.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import ua.foxminded.car.microservice.entities.Car;
import ua.foxminded.car.microservice.service.CarService;
import ua.foxminded.university.dao.validation.InfoConstants;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createCar(@RequestBody Car car) throws EntityExistsException {
        return carService.createCar(car);
    }

    @GetMapping("/{objectId}")
    public ResponseEntity<Car> getCarById(@PathVariable UUID objectId) {
        Optional<Car> optionalCar = carService.findCarById(objectId);
        if (!optionalCar.isPresent()) {
            throw new EntityNotFoundException(InfoConstants.CAR_NOT_FOUND);
        }
        return ResponseEntity.ok(optionalCar.get());
    }

    @GetMapping
    public List<Car> getAllCars(Pageable pageable) {
        return carService.listAllCars();
    }
}
