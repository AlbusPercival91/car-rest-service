package ua.foxminded.car.microservice.dao.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.car.microservice.dao.entities.Car;
import ua.foxminded.car.microservice.dao.repository.CarRepository;
import ua.foxminded.car.microservice.validation.InfoConstants;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CarService {
    private final CarRepository carRepository;

    public UUID createCar(Car car) {
        if (carRepository.findById(car.getObjectId()).isPresent()) {
            log.warn(InfoConstants.CAR_EXISTS);
            throw new EntityExistsException(InfoConstants.CAR_EXISTS);
        }
        Car newCar = carRepository.save(car);
        log.info(InfoConstants.CREATE_SUCCESS);
        return newCar.getObjectId();
    }

    public UUID deleteCarById(UUID carId) {
        if (carRepository.findById(carId).isEmpty()) {
            log.warn(InfoConstants.CAR_NOT_FOUND);
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
        log.info(InfoConstants.UPDATE_SUCCESS);
        return carRepository.save(existingCar);
    }

    public int assignCarToCategory(UUID carId, String categoryName) {
        if (!carRepository.existsByObjectIdAndCategoriesCategoryName(carId, categoryName)) {
            log.info(InfoConstants.ASSIGN_SUCCESS);
            return carRepository.assignCarToCategory(carId, categoryName);
        }
        log.warn(InfoConstants.CAR_BELONGS_TO_CATEGORY);
        throw new IllegalStateException(InfoConstants.CAR_BELONGS_TO_CATEGORY);
    }

    public int removeCarFromCategory(UUID carId, String categoryName) {
        if (carRepository.existsByObjectIdAndCategoriesCategoryName(carId, categoryName)) {
            log.info(InfoConstants.DELETE_SUCCESS);
            return carRepository.removeCarFromCategory(carId, categoryName);
        }
        log.warn(InfoConstants.CAR_NOT_BELONGS_TO_CATEGORY);
        throw new IllegalStateException(InfoConstants.CAR_NOT_BELONGS_TO_CATEGORY);
    }

    public Optional<Car> findCarById(UUID carId) {
        return carRepository.findById(carId);
    }

    public Page<Car> listAllCars(int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        return carRepository.findAll(pageRequest);
    }

    public Page<Car> findCarsByCategory(String categoryName, int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        return carRepository.findAllByCategory(categoryName, pageRequest);
    }

    public Page<Car> findCarsByMake(String make, int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        return carRepository.findAllByMake(make, pageRequest);
    }

    public Page<Car> findCarsByModel(String model, int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        return carRepository.findAllByModel(model, pageRequest);
    }

    public Page<Car> findCarsByYearBetween(int yearFrom, int yearTo, int page, int size, String sortBy,
            String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        return carRepository.findAllByYearBetween(yearFrom, yearTo, pageRequest);
    }

    public Page<Car> searchCars(String make, String model, Integer minYear, Integer maxYear, String categoryName,
            Pageable pageable) {
        if (categoryName != null && !categoryName.isEmpty()) {
            return carRepository.findAllByCategory(categoryName, pageable);
        } else if (make != null && !make.isEmpty()) {
            return carRepository.findAllByMake(make, pageable);
        } else if (model != null && !model.isEmpty()) {
            return carRepository.findAllByModel(model, pageable);
        } else if (minYear != null && maxYear != null) {
            return carRepository.findAllByYearBetween(minYear, maxYear, pageable);
        } else {
            return carRepository.findAll(pageable);
        }
    }

}
