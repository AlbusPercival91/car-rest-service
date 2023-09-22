package ua.foxminded.car.microservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.car.microservice.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String categoryName);
}
