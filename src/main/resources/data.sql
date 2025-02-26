-- Вставляем продукты
INSERT INTO products (name) VALUES
                                ('Table'),
                                ('Chair'),
                                ('Desk'),
                                ('Sofa'),
                                ('Lamp');

-- Вставляем поставщиков
INSERT INTO suppliers (name) VALUES
                                 ('Supplier A'),
                                 ('Supplier B'),
                                 ('Supplier C');

-- Вставляем дефолтные зоны (цвет в формате HEX)
INSERT INTO zones (name, color) VALUES
                                    ('RECEIVING', '#00FF00'),
                                    ('SHIPMENT', '#FF0000'),
                                    ('STORAGE', '#0000FF');

-- Вставляем пользователей (условно зашифрованные пароли)
INSERT INTO users (username, password, role) VALUES
                                                 ('manager', 'encrypted_password', 'WAREHOUSE_MANAGER'),
                                                 ('operator1', 'encrypted_password', 'OPERATOR'),
                                                 ('operator2', 'encrypted_password', 'OPERATOR');

-- Вставляем партии товаров.
-- Согласно бизнес-логике:
-- Партия 1: Только создана, статус = GENERATED, зона = RECEIVING.
-- Партия 2: Все коробки обработаны (QR‑коды нанесены), статус = STICKED, зона = STORAGE.
-- Партия 3: Все коробки отсканированы, статус = SCANNED, зона = SHIPPMENT.
-- Партия 4: Только создана, статус = GENERATED, зона = RECEIVING.
-- Партия 5: Не все коробки отсканированы (2 из 3 CONFIRMED, 1 GENERATED), агрегированный статус остаётся STICKED.
INSERT INTO product_batches (product_id, supplier_id, received_at, status, zone_id) VALUES
                                                                                        (1, 1, CURRENT_TIMESTAMP - INTERVAL '2 days', 'GENERATED', (SELECT id FROM zones WHERE name='RECEIVING')),
                                                                                        (2, 2, CURRENT_TIMESTAMP - INTERVAL '1 day', 'STICKED', (SELECT id FROM zones WHERE name='STORAGE')),
                                                                                        (3, 3, CURRENT_TIMESTAMP, 'SCANNED', (SELECT id FROM zones WHERE name='SHIPMENT')),
                                                                                        (4, 1, CURRENT_TIMESTAMP - INTERVAL '3 days', 'GENERATED', (SELECT id FROM zones WHERE name='RECEIVING')),
                                                                                        (5, 2, CURRENT_TIMESTAMP - INTERVAL '5 hours', 'STICKED', (SELECT id FROM zones WHERE name='STORAGE'));

-- Вставляем коробки для каждой партии.
-- Партия 1 (id=1): 3 коробки, все в состоянии GENERATED.
INSERT INTO boxes (code, product_batch_id, status) VALUES
                                                       (NULL, 1, 'GENERATED'),
                                                       (NULL, 1, 'GENERATED'),
                                                       (NULL, 1, 'GENERATED');

-- Партия 2 (id=2): 2 коробки, обе в состоянии STICKED.
INSERT INTO boxes (code, product_batch_id, status) VALUES
                                                       (NULL, 2, 'STICKED'),
                                                       (NULL, 2, 'STICKED');

-- Партия 3 (id=3): 4 коробки, все в состоянии SCANNED.
-- Здесь добавляем scanned_at и scanned_by (оператор1, id=2).
INSERT INTO boxes (code, product_batch_id, status, scanned_at, scanned_by) VALUES
                                                                               (NULL, 3, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 2),
                                                                               (NULL, 3, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '25 minutes', 2),
                                                                               (NULL, 3, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '20 minutes', 2),
                                                                               (NULL, 3, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '15 minutes', 2);

-- Партия 4 (id=4): 2 коробки, обе в состоянии GENERATED.
INSERT INTO boxes (code, product_batch_id, status) VALUES
                                                       (NULL, 4, 'GENERATED'),
                                                       (NULL, 4, 'GENERATED');

-- Партия 5 (id=5): 3 коробки, 2 в состоянии SCANNED, 1 в состоянии GENERATED.
-- Поэтому агрегированный статус партии остаётся STICKED.
INSERT INTO boxes (code, product_batch_id, status, scanned_at, scanned_by) VALUES
                                                                               (NULL, 5, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '40 minutes', 2),
                                                                               (NULL, 5, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '35 minutes', 2),
                                                                               (NULL, 5, 'GENERATED',NULL, NULL);

-- Вставляем события сканирования (примерные данные)
-- Предположим, что для партии 3 коробки (id 6,7,8,9) были отсканированы.
INSERT INTO scan_events (box_id, user_id, scan_mode, scan_time) VALUES
                                                                    ((SELECT id FROM boxes WHERE product_batch_id = 3 LIMIT 1), 2, 'ON_WAREHOUSE', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
                                                                    ((SELECT id FROM boxes WHERE product_batch_id = 3 ORDER BY id LIMIT 1 OFFSET 1), 2, 'ON_WAREHOUSE', CURRENT_TIMESTAMP - INTERVAL '25 minutes'),
                                                                    ((SELECT id FROM boxes WHERE product_batch_id = 3 ORDER BY id LIMIT 1 OFFSET 2), 2, 'ON_WAREHOUSE', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
                                                                    ((SELECT id FROM boxes WHERE product_batch_id = 3 ORDER BY id LIMIT 1 OFFSET 3), 2, 'ON_WAREHOUSE', CURRENT_TIMESTAMP - INTERVAL '15 minutes');
