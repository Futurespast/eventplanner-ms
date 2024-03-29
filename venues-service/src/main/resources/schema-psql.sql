DROP TABLE IF EXISTS venue_available_dates CASCADE;
DROP TABLE IF EXISTS venues CASCADE;

CREATE TABLE if not exists venues (
                                      id SERIAL,
                                      venue_id VARCHAR(255),
    street_address VARCHAR(255),
    city VARCHAR(255),
    province VARCHAR(255),
    country VARCHAR(255),
    postal_code VARCHAR(255),
    name VARCHAR(255),
    capacity INT,
    PRIMARY KEY (id)
    );

CREATE TABLE if not exists venue_available_dates (
                                                     venue_id INT,
                                                     available_dates DATE
);