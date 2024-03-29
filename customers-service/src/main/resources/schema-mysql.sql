
CREATE TABLE if not exists customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(36),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email_address VARCHAR(255),
    street_address VARCHAR(255),
    city VARCHAR(255),
    province VARCHAR(255),
    country VARCHAR(255),
    postal_code VARCHAR(20)
    );

CREATE TABLE if not exists customer_phonenumbers (
    customer_id INT,
    phone_type VARCHAR(10),
    number VARCHAR(20)
    );