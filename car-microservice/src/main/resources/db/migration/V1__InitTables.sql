CREATE SCHEMA IF NOT EXISTS vehicle AUTHORIZATION car_admin;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vehicle.car (
    object_id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    make VARCHAR(70) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicle.category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicle.cars_categories (
    id SERIAL PRIMARY KEY,
    car_id UUID,
    category_id INT,
    FOREIGN KEY (car_id) REFERENCES vehicle.car (object_id),
    FOREIGN KEY (category_id) REFERENCES vehicle.category (category_id)
);