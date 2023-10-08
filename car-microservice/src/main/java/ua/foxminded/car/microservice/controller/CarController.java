package ua.foxminded.car.microservice.controller;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import ua.foxminded.car.microservice.dao.entities.Car;
import ua.foxminded.car.microservice.dao.service.CarService;
import ua.foxminded.car.microservice.validation.InfoConstants;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @Operation(summary = InfoConstants.CREATE_CAR, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.CREATE_SUCCESS, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class)) }),
            @ApiResponse(responseCode = "409", description = InfoConstants.CAR_EXISTS, content = @Content) })
    @PostMapping
    public ResponseEntity<UUID> createCar(@RequestBody Car car) {
        try {
            UUID carId = carService.createCar(car);
            return ResponseEntity.ok(carId);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = InfoConstants.DELETE_CAR, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = InfoConstants.DELETE_SUCCESS, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class)) }),
            @ApiResponse(responseCode = "404", description = InfoConstants.CAR_NOT_FOUND, content = @Content) })
    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carId) {
        try {
            carService.deleteCarById(carId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = InfoConstants.UPDATE_CAR, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.UPDATE_SUCCESS, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class)) }),
            @ApiResponse(responseCode = "404", description = InfoConstants.CAR_NOT_FOUND, content = @Content) })
    @PutMapping("/{carId}")
    public ResponseEntity<Car> updateCar(@PathVariable UUID carId, @RequestBody Car car) {
        try {
            Car updatedCar = carService.updateCarById(carId, car);
            return ResponseEntity.ok(updatedCar);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = InfoConstants.ASSIGN_CAR, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.ASSIGN_SUCCESS, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class)) }),
            @ApiResponse(responseCode = "409", description = InfoConstants.CAR_BELONGS_TO_CATEGORY, content = @Content) })
    @PostMapping("/{carId}/assign-category")
    public ResponseEntity<Void> assignCarToCategory(@PathVariable UUID carId, @RequestParam String categoryName) {
        try {
            carService.assignCarToCategory(carId, categoryName);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = InfoConstants.REASSIGN_CAR, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.DELETE_SUCCESS, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class)) }),
            @ApiResponse(responseCode = "409", description = InfoConstants.CAR_NOT_BELONGS_TO_CATEGORY, content = @Content) })
    @PostMapping("/{carId}/remove-category")
    public ResponseEntity<Void> removeCarFromCategory(@PathVariable UUID carId, @RequestParam String categoryName) {
        try {
            carService.removeCarFromCategory(carId, categoryName);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = InfoConstants.GET_CAR_BY_ID)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = InfoConstants.CAR_FOUND, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class)) }),
            @ApiResponse(responseCode = "404", description = InfoConstants.CAR_NOT_FOUND, content = @Content) })
    @GetMapping("/{carId}")
    public ResponseEntity<Car> getCar(@PathVariable UUID carId) {
        Optional<Car> car = carService.findCarById(carId);
        return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = InfoConstants.LIST_CARS)
    @ApiResponse(responseCode = "200", description = InfoConstants.CARS_LISTED, content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class)) })
    @GetMapping
    public ResponseEntity<Page<Car>> listCars(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "make") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Page<Car> carsPage = carService.listAllCars(page, size, sortBy, sortOrder);

        return ResponseEntity.ok(carsPage);
    }

    @Operation(summary = InfoConstants.SEARCH_CARS)
    @ApiResponse(responseCode = "200", description = InfoConstants.CARS_LISTED, content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class)) })
    @GetMapping("/search")
    public ResponseEntity<Page<Car>> searchCars(@RequestParam(required = false) String make,
            @RequestParam(required = false) String model, @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear, @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "make") String sortBy, @RequestParam(defaultValue = "asc") String sortOrder) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);

        Page<Car> searchResult = carService.searchCars(make, model, minYear, maxYear, categoryName, pageRequest);

        return ResponseEntity.ok(searchResult);
    }
}