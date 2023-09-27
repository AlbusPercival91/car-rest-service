package ua.foxminded.car.microservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.foxminded.car.microservice.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategoryName(String categoryName);

    @Query("""
            SELECT c FROM Category ct
                 JOIN ct.cars c
                 WHERE c.carId = :carId
            """)
    List<Category> getCarCategories(@Param("carId") UUID carId);
}
