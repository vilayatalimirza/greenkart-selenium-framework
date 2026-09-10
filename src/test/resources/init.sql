-- src/test/resources/init.sql
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50)
);

-- Seed the baseline data required for our UI-DB Validation tests
INSERT INTO products (product_name, price, category) VALUES 
('Brocolli', 120.00, 'Vegetable'),
('Cauliflower', 60.00, 'Vegetable'),
('Cucumber', 48.00, 'Vegetable'),
('Beetroot', 32.00, 'Vegetable');