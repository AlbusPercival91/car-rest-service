-- Generate test data for vehicle.car
INSERT INTO vehicle.car (make, model, year)
VALUES
    ('VW', 'Jetta', 2006),
    ('Hyundai', 'Getz', 2007),
    ('Audi', 'Q3', 2011),
    ('Scoda', 'Octavia', 2013);

-- Generate test data for vehicle.category
INSERT INTO vehicle.category (category_name)
VALUES
    ('SUV'),
    ('Sedan'),
    ('Hatchback');

-- Insert sample data for vehicle.cars_categories
INSERT INTO vehicle.cars_categories (car_id, category_id)
SELECT
    c.object_id,  
    ct.category_id 
FROM
   vehicle.car AS c
    JOIN LATERAL (
        SELECT category_id
        FROM vehicle.category
        WHERE category_id NOT IN (
            SELECT category_id
            FROM vehicle.cars_categories
            WHERE car_id = c.object_id
        )
        ORDER BY random()
        LIMIT 5 
    ) AS ct ON true;    

    
    
    
    
    
    
