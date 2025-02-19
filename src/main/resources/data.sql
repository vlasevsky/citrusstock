-- Insert sample products
INSERT INTO products (name) VALUES ('Table'), ('Chair'), ('Desk');

-- Insert sample suppliers
INSERT INTO suppliers (name) VALUES ('Supplier A'), ('Supplier B');

-- Insert a sample product batch (используем product_id = 1 и supplier_id = 1)
-- Поля: product_id, supplier_id, received_at, status, zone
INSERT INTO product_batches (product_id, supplier_id, received_at, status, zone)
VALUES (1, 1, CURRENT_TIMESTAMP, 'WAITING_FOR_SCANNING', 'RECEIVING');

-- Insert sample boxes for the product batch with id = 1
-- Количество коробок определяется количеством вставляемых записей, статус изначально GENERATED, поле code пустое (NULL)
INSERT INTO boxes (code, product_batch_id, status)
VALUES (NULL, 1, 'GENERATED'),
       (NULL, 1, 'GENERATED'),
       (NULL, 1, 'GENERATED');

-- Insert sample users
INSERT INTO users (username, password, role)
VALUES ('manager', 'password', 'WAREHOUSE_MANAGER'),
       ('operator1', 'password', 'OPERATOR');

-- Insert a sample scan event (если требуется)
INSERT INTO scan_events (box_id, user_id, scan_mode, scan_time)
VALUES (1, 2, 'ON_WAREHOUSE', CURRENT_TIMESTAMP);
