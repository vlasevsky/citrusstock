-- Insert sample products
INSERT INTO products (name) VALUES ('Table'), ('Chair'), ('Desk');

-- Insert sample suppliers
INSERT INTO suppliers (name) VALUES ('Supplier A'), ('Supplier B');

-- Insert a sample product batch (using product_id = 1 and supplier_id = 1)
-- Set total_boxes to 3, status to 'WAITING_FOR_SCANNING' and zone to 'RECEIVING'
INSERT INTO product_batches (product_id, supplier_id, total_boxes, received_at, status, zone)
VALUES (1, 1, 3, CURRENT_TIMESTAMP, 'WAITING_FOR_SCANNING', 'RECEIVING');

-- Insert sample boxes for the product batch with id = 1
INSERT INTO boxes (code, product_batch_id, status)
VALUES ('boxCode1', 1, 'GENERATED'),
       ('boxCode2', 1, 'GENERATED'),
       ('boxCode3', 1, 'GENERATED');

-- Insert sample users
INSERT INTO users (username, password, role)
VALUES ('manager', 'password', 'WAREHOUSE_MANAGER'),
       ('operator1', 'password', 'OPERATOR');

-- Insert a sample scan event (if needed)
INSERT INTO scan_events (box_id, user_id, scan_mode, scan_time)
VALUES (1, 2, 'ON_WAREHOUSE', CURRENT_TIMESTAMP);
