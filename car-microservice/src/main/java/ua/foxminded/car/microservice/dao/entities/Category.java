package ua.foxminded.car.microservice.dao.entities;

import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "category", schema = "vehicle")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private Set<Car> cars = new HashSet<>();

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

}
