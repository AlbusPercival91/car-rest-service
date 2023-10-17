package ua.foxminded.car.microservice.dao.entities;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cars_categories", schema = "vehicle")
public class CarsCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "car_id")
    private UUID carId;

    @Column(name = "category_id")
    private int categoryId;

}
