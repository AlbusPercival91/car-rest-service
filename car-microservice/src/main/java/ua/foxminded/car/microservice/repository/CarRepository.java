package ua.foxminded.car.microservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.car.microservice.entities.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

}
