CREATE ROLE car_admin 
LOGIN
PASSWORD '1234';


CREATE DATABASE car
    WITH
    OWNER = car_admin
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
    
GRANT ALL PRIVILEGES ON DATABASE car TO car_admin;



    