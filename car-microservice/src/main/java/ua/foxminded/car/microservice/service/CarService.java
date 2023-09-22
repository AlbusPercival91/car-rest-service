package ua.foxminded.car.microservice.service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.car.microservice.repository.CarRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CarService {
    private final CarRepository carRepository;
    
}
