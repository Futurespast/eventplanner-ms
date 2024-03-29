INSERT INTO customers (customer_id, first_name, last_name, email_address, street_address, city, province, country, postal_code)
VALUES('c3540a89-cb47-4c96-888e-ff96708db4d8', 'John', 'Doe', 'john.doe@example.com', '123 Maple Street', 'Anytown', 'Ontario', 'Canada', 'A1B 2C3'),
      ('a1b2c3d4-e5f6-7g8h-9i0j-1k2l3m4n5o6p', 'Jane', 'Smith', 'jane.smith@example.com', '456 Oak Lane', 'Anytown', 'Ontario', 'Canada', 'A4B 5C6'),
      ('b2c3d4e5-f6g7-8h9i-0j1k-2l3m4n5o6p7q', 'Alice', 'Johnson', 'alice.johnson@example.com', '789 Pine Avenue', 'Anytown', 'Ontario', 'Canada', 'A7B 8C9'),
      ('c3d4e5f6-g7h8-9i0j-1k2l-3m4n5o6p7q8r', 'Bob', 'Brown', 'bob.brown@example.com', '101 Ash Street', 'Anytown', 'Ontario', 'Canada', 'A1A 1A1'),
      ('d4e5f6g7-h8i9-0j1k-2l3m-4n5o6p7q8r9s', 'Carol', 'Davis', 'carol.davis@example.com', '202 Birch Road', 'Anytown', 'Ontario', 'Canada', 'B2C 3D4'),
      ('e5f6g7h8-i9j0-1k2l-3m4n-5o6p7q8r9s0t', 'David', 'Wilson', 'david.wilson@example.com', '303 Cedar Lane', 'Anytown', 'Ontario', 'Canada', 'C5D 6E7'),
      ('f6g7h8i9-j0k1-2l3m-4n5o-6p7q8r9s0t1u', 'Eva', 'Martinez', 'eva.martinez@example.com', '404 Elm Street', 'Anytown', 'Ontario', 'Canada', 'D8F 9G0'),
      ('g7h8i9j0-k1l2-3m4n-5o6p-7q8r9s0t1u2v', 'Frank', 'Garcia', 'frank.garcia@example.com', '505 Fir Avenue', 'Anytown', 'Ontario', 'Canada', 'G1H 2J3'),
      ('h8i9j0k1-l2m3-4n5o-6p7q-8r9s0t1u2v3w', 'Grace', 'Lee', 'grace.lee@example.com', '606 Hickory Road', 'Anytown', 'Ontario', 'Canada', 'J4K 5L6'),
      ('i9j0k1l2-m3n4-5o6p-7q8r-9s0t1u2v3w4x', 'Henry', 'Anderson', 'henry.anderson@example.com', '707 Ivy Lane', 'Anytown', 'Ontario', 'Canada', 'L7M 8N9');
INSERT INTO customer_phonenumbers (customer_id,phone_type,number)
VALUES(1, 'MOBILE', '555-1234'),(1, 'HOME', '555-5678'),
      (2, 'MOBILE', '555-2345'),
      (2, 'HOME', '555-6789'),
      (3, 'MOBILE', '555-3456'),
      (3, 'HOME', '555-7890'),
      (4, 'MOBILE', '555-4567'),
      (4, 'HOME', '555-8901'),
      (5, 'MOBILE', '555-5678'),
      (5, 'HOME', '555-9012'),
      (6, 'MOBILE', '555-6789'),
      (6, 'HOME', '555-0123'),
      (7, 'MOBILE', '555-7890'),
      (7, 'HOME', '555-1234'),
      (8, 'MOBILE', '555-8901'),
      (8, 'HOME', '555-2345'),
      (9, 'MOBILE', '555-9012'),
      (9, 'HOME', '555-3456');