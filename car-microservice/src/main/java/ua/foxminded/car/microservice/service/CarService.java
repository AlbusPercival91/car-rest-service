package ua.foxminded.car.microservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.car.microservice.entities.Car;
import ua.foxminded.car.microservice.repository.CarRepository;
import ua.foxminded.university.dao.validation.InfoConstants;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CarService {
    private final CarRepository carRepository;

    public UUID createCar(Car car) {
        if (carRepository.findById(car.getObjectId()).isPresent()) {
            log.info(InfoConstants.CAR_EXISTS);
            throw new EntityExistsException(InfoConstants.CAR_EXISTS);
        }
        Car newCar = carRepository.save(car);
        log.info(InfoConstants.CREATE_SUCCESS);
        return newCar.getObjectId();
    }

    public UUID deleteCarById(UUID carId) {
        if (carRepository.findById(carId).isEmpty()) {
            log.info(InfoConstants.CAR_NOT_FOUND);
            throw new EntityNotFoundException(InfoConstants.CAR_NOT_FOUND);
        }
        carRepository.deleteById(carId);
        log.info(InfoConstants.DELETE_SUCCESS);
        return carId;
    }

    public Car updateCarById(UUID carId, Car targetCar) {
        Car existingCar = carRepository.findById(carId).orElseThrow(() -> {
            log.warn(InfoConstants.CAR_NOT_FOUND);
            return new EntityNotFoundException(InfoConstants.CAR_NOT_FOUND);
        });

        BeanUtils.copyProperties(targetCar, existingCar, "objectId");
        return carRepository.save(existingCar);
    }

    public int assignCarToCategory(UUID carId, String categoryName) {
        return assignCarToCategory(carId, categoryName);
    }

    public int removeCarFromCategory(UUID carId, String categoryName) {
        return assignCarToCategory(carId, categoryName);
    }

    public Optional<Car> findCarById(UUID carId) {
        return carRepository.findById(carId);
    }

    public List<Car> listAllCars() {
        return carRepository.findAll(Sort.by(Sort.Direction.ASC, "make"));
    }

    public List<Car> findCarsByCategory(String categoryName) {
        return carRepository.findAllByCategoryOrderByMakeAsc(categoryName);
    }

    public List<Car> findCarsByMake(String make) {
        return carRepository.findAllByMakeOrderByMakeAsc(make);
    }

    public List<Car> findCarsByModel(String model) {
        return carRepository.findAllByModelOrderByMakeAsc(model);
    }

    public List<Car> findCarsByYearBetween(int yearFrom, int yearTo) {
        return carRepository.findAllByYearBetween(yearFrom, yearTo);
    }

}
