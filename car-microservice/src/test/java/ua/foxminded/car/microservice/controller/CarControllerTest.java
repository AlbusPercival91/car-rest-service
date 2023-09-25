package ua.foxminded.car.microservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.foxminded.car.microservice.entities.Car;
import ua.foxminded.car.microservice.service.CarService;

@WebMvcTest({ CarController.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    void createCarTest() throws Exception {
        Car car = new Car("Toyota", "Camry", 2011);
        UUID carId = UUID.randomUUID();

        when(carService.createCar(any(Car.class))).thenReturn(carId);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(car))).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(carId.toString()));
    }

    @Test
    void getCarTest() throws Exception {
        UUID carId = UUID.randomUUID();

        when(carService.findCarById(carId)).thenReturn(Optional.of(new Car()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/{carId}", carId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateCarTest() throws Exception {
        UUID carId = UUID.randomUUID();

        Car updatedCar = new Car("Toyota", "Camry", 2011);

        when(carService.updateCarById(carId, updatedCar)).thenReturn(updatedCar);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cars/{carId}", carId)
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(updatedCar)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
