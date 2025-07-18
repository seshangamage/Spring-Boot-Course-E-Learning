-- MySQL Database Setup Script for Laptop Store API

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS laptop_store_db;

-- Use the database
USE laptop_store_db;

-- Create user for the application (optional - you can use root)
-- CREATE USER IF NOT EXISTS 'laptop_user'@'localhost' IDENTIFIED BY 'laptop_password';
-- GRANT ALL PRIVILEGES ON laptop_store_db.* TO 'laptop_user'@'localhost';
-- FLUSH PRIVILEGES;

-- The laptops table will be auto-created by Hibernate with the following structure:
-- 
-- CREATE TABLE laptops (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     brand VARCHAR(50) NOT NULL,
--     model VARCHAR(100) NOT NULL,
--     price DECIMAL(10,2) NOT NULL,
--     description VARCHAR(500),
--     processor VARCHAR(100) NOT NULL,
--     ram_size_gb INT NOT NULL,
--     storage_size_gb INT NOT NULL,
--     storage_type VARCHAR(50),
--     screen_size VARCHAR(50),
--     created_at TIMESTAMP NOT NULL,
--     updated_at TIMESTAMP,
--     INDEX idx_brand (brand),
--     INDEX idx_model (model),
--     INDEX idx_price (price),
--     INDEX idx_ram_size (ram_size_gb),
--     INDEX idx_storage_size (storage_size_gb)
-- );

-- Show database status
SHOW DATABASES;
SELECT 'MySQL database setup completed successfully!' as message;
