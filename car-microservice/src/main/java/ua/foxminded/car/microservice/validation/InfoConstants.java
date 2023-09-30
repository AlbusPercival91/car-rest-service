package ua.foxminded.car.microservice.validation;

public final class InfoConstants {

    private InfoConstants() {

    }

    public static final String CAR_EXISTS = "Car already exists";
    public static final String CATEGORY_EXISTS = "Category already exists";
    public static final String CREATE_SUCCESS = "Created successfully";
    public static final String DELETE_SUCCESS = "Deleted successfully";
    public static final String ASSIGN_SUCCESS = "Assigned successfully";
    public static final String CAR_NOT_FOUND = "Car not found";
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String CAR_BELONGS_TO_CATEGORY = "Car already assigned to this category";
    public static final String CAR_NOT_BELONGS_TO_CATEGORY = "Car is not assigned to this category";

}
