package ua.foxminded.car.microservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cars_categories", schema = "vehicle")
public class CarsCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "car_id")
    private String carId;

    @Column(name = "category_id")
    private int categoryId;

    public CarsCategories(String carId, int categoryId) {
        this.carId = carId;
        this.categoryId = categoryId;
    }

}
