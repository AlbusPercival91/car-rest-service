package ua.foxminded.car.microservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import ua.foxminded.car.microservice.dao.entities.Car;
import ua.foxminded.car.microservice.dao.service.CarService;
import ua.foxminded.car.microservice.validation.InfoConstants;

@WebMvcTest({ CarController.class })
@ActiveProfiles("test-container")
@WithMockUser(roles = "ADMIN")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    void testCreateCar_Success_ShouldGiveStatusIsOk() throws Exception {
        Car car = new Car("Toyota", "Camry", 2011);
        UUID carId = UUID.randomUUID();

        when(carService.createCar(any(Car.class))).thenReturn(carId);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars").with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(car)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(carId.toString()));
    }

    @Test
    void testCreateCar_Failure_ShouldGiveStatusIsConflict() throws Exception {
        Car car = new Car("Toyota", "Camry", 2011);

        when(carService.createCar(any(Car.class))).thenThrow(new EntityExistsException(InfoConstants.CAR_EXISTS));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars").with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(car)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void testDeleteCar_Success_ShouldGiveStatusIsNoContent() throws Exception {
        UUID carId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/{carId}", carId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testDeleteCar_Failure_ShouldGiveStatusIsNotFound() throws Exception {
        UUID carId = UUID.randomUUID();

        when(carService.deleteCarById(carId)).thenThrow(new EntityNotFoundException(InfoConstants.CAR_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/{carId}", carId).with(csrf().asHeader()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testUpdateCar_Success_ShouldGiveStatusIsOk() throws Exception {
        UUID carId = UUID.randomUUID();
        Car updatedCar = new Car("Toyota", "Camry", 2011);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cars/{carId}", carId).with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(updatedCar)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testUpdateCar_Failure_ShouldGiveStatusIsNotFound() throws Exception {
        UUID carId = UUID.randomUUID();
        Car updatedCar = new Car("Toyota", "Camry", 2011);

        when(carService.updateCarById(eq(carId), any(Car.class)))
                .thenThrow(new EntityNotFoundException(InfoConstants.CAR_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cars/{carId}", carId).with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(updatedCar)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testAssignCarToCategory_Success_ShouldGiveStatusIsOk() throws Exception {
        UUID carId = UUID.randomUUID();
        String categoryName = "Sedan";

        when(carService.assignCarToCategory(carId, categoryName)).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars/{carId}/assign-category", carId)
                .with(csrf().asHeader()).param("categoryName", categoryName))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testAssignCarToCategory_Failure_ShouldGiveStatusIsConflict() throws Exception {
        UUID carId = UUID.randomUUID();
        String categoryName = "Sedan";

        when(carService.assignCarToCategory(carId, categoryName))
                .thenThrow(new IllegalStateException(InfoConstants.CAR_BELONGS_TO_CATEGORY));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars/{carId}/assign-category", carId)
                .with(csrf().asHeader()).param("categoryName", categoryName))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void testRemoveCarFromCategory_Success_ShouldGiveStatusIsOk() throws Exception {
        UUID carId = UUID.randomUUID();
        String categoryName = "Sedan";

        when(carService.removeCarFromCategory(carId, categoryName)).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars/{carId}/remove-category", carId)
                .with(csrf().asHeader()).param("categoryName", categoryName))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveCarFromCategory_Failure_ShouldGiveStatusIsConflict() throws Exception {
        UUID carId = UUID.randomUUID();
        String categoryName = "Sedan";

        when(carService.removeCarFromCategory(carId, categoryName))
                .thenThrow(new IllegalStateException(InfoConstants.CAR_NOT_BELONGS_TO_CATEGORY));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars/{carId}/remove-category", carId)
                .with(csrf().asHeader()).param("categoryName", categoryName))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void testGetCar_Success_ShouldGiveStatusIsOk() throws Exception {
        UUID carId = UUID.randomUUID();

        when(carService.findCarById(carId)).thenReturn(Optional.of(new Car()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/{carId}", carId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @CsvSource({ "10, model, desc" })
    void testListCars_Success_ShouldGiveStatusIsOk(int size, String sortBy, String sortOrder) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars").param("size", String.valueOf(size))
                .param("sortBy", sortBy).param("sortOrder", sortOrder))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @CsvSource({ "testCategory, Nissan, Skyline, 2002, 2004, 0, 10, model, desc" })
    void testSearchCars_Success_ShouldGiveStatusIsOk(String categoryName, String make, String model, int minYear,
            int maxYear, int page, int size, String sortBy, String sortOrder) throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/search").param("categoryName", categoryName)
                .param("carId", UUID.randomUUID().toString()).param("page", String.valueOf(page))
                .param("size", String.valueOf(size)).param("sortBy", sortBy).param("sortOrder", sortOrder))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
