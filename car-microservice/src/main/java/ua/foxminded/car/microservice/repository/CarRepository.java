package ua.foxminded.car.microservice.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.foxminded.car.microservice.entities.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    @Modifying
    @Query("""
             INSERT INTO CarsCategories (carId, categoryId)
                 SELECT c.objectId, ct.categoryId FROM Car c, Category ct
                 WHERE c.objectId = :carId AND ct.categoryName = :categoryName
            """)
    int assignCarToCategory(@Param("carId") UUID carId, @Param("categoryName") String categoryName);

    @Modifying
    @Query("""
             DELETE FROM CarsCategories cCt
                 WHERE cCt.carId = :carId
                 AND cCt.categoryId IN (SELECT ct.categoryId FROM Category ct WHERE ct.categoryName = :categoryName)
            """)
    int removeCarFromCategory(@Param("carId") UUID carId, @Param("categoryName") String categoryName);

    @Query("""
            SELECT c FROM Car c
                 JOIN CarsCategories cCt ON c.objectId = cCt.carId
                 JOIN Category ct ON ct.categoryId = cCt.categoryId WHERE ct.categoryName = :categoryName
            """)
    Page<Car> findAllByCategory(@Param("categoryName") String categoryName, Pageable pageable);

    Page<Car> findAllByMake(String make, Pageable pageable);

    Page<Car> findAllByModel(String model, Pageable pageable);

    Page<Car> findAllByYearBetween(int yearFrom, int yearTo, Pageable pageable);
}
