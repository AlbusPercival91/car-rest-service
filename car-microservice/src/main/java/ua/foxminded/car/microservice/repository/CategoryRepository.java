package ua.foxminded.car.microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.car.microservice.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
