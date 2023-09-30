package ua.foxminded.car.microservice.dao.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ua.foxminded.car.microservice.dao.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategoryName(String categoryName);

    @Query("""
            SELECT ct FROM Category ct
                 JOIN ct.cars c
                 WHERE c.objectId = :carId
            """)
    Page<Category> getCarCategories(@Param("carId") UUID carId, Pageable pageable);
}