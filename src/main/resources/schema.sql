-- Drop tables if they exist (order matters due to foreign key constraints)
DROP TABLE IF EXISTS scan_events;
DROP TABLE IF EXISTS boxes;
DROP TABLE IF EXISTS product_batches;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;

-- Create Products table
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL
);

-- Create Suppliers table
CREATE TABLE suppliers (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL
);

-- Create Product Batches table
CREATE TABLE product_batches (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 supplier_id BIGINT NOT NULL,
                                 total_boxes INT NOT NULL,
                                 received_at TIMESTAMP NOT NULL,
                                 status VARCHAR(50) NOT NULL,
                                 FOREIGN KEY (product_id) REFERENCES products(id),
                                 FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- Create Boxes table
CREATE TABLE boxes (
                       id BIGSERIAL PRIMARY KEY,
                       code VARCHAR(255),
                       product_batch_id BIGINT NOT NULL,
                       status VARCHAR(50),
                       scanned_at TIMESTAMP,
                       scanned_by BIGINT,
                       FOREIGN KEY (product_batch_id) REFERENCES product_batches(id)
);

-- Create Users table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

-- Create Scan Events table
CREATE TABLE scan_events (
                             id BIGSERIAL PRIMARY KEY,
                             box_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             scan_mode VARCHAR(50) NOT NULL,
                             scan_time TIMESTAMP NOT NULL,
                             FOREIGN KEY (box_id) REFERENCES boxes(id),
                             FOREIGN KEY (user_id) REFERENCES users(id)
);
