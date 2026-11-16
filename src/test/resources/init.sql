-- Canonical seed for a disposable GreenKart automation test database.
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50)
);

INSERT INTO products (product_name, price, category) VALUES
('Cucumber', 30.00, 'Vegetable'),
('Brocolli', 60.00, 'Vegetable'),
('Beetroot', 40.00, 'Vegetable');
