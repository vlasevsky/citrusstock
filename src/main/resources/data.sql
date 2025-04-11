-- Вставляем роли
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Администратор системы с полным доступом'),
    ('MANAGER', 'Менеджер склада с доступом к управлению товарами'),
    ('OPERATOR', 'Оператор склада с доступом к сканированию');

-- Вставляем права доступа
INSERT INTO permissions (name, description, category) VALUES
    -- Права администратора
    ('admin:manage_users', 'Управление пользователями системы', 'admin'),
    ('admin:manage_roles', 'Управление ролями и правами доступа', 'admin'),
    ('admin:view_all', 'Просмотр всех данных системы', 'admin'),
    
    -- Права для пользователей
    ('users:read', 'Просмотр информации о пользователях', 'users'),
    ('users:create', 'Создание новых пользователей', 'users'),
    ('users:update', 'Обновление существующих пользователей', 'users'),
    ('users:delete', 'Удаление пользователей', 'users'),
    
    -- Права для коробок
    ('boxes:read', 'Просмотр информации о коробках', 'boxes'),
    ('boxes:create', 'Создание новых коробок', 'boxes'),
    ('boxes:update', 'Обновление существующих коробок', 'boxes'),
    ('boxes:delete', 'Удаление коробок', 'boxes'),
    
    -- Права для сканирования
    ('scan:read', 'Просмотр истории сканирования', 'scan'),
    ('scan:create', 'Сканирование коробок', 'scan'),
    
    -- Права для партий продуктов
    ('batches:read', 'Просмотр информации о партиях продуктов', 'batches'),
    ('batches:create', 'Создание новых партий продуктов', 'batches'),
    ('batches:update', 'Обновление существующих партий', 'batches'),
    ('batches:delete', 'Удаление партий продуктов', 'batches'),
    
    -- Права для продуктов
    ('products:read', 'Просмотр информации о продуктах', 'products'),
    ('products:create', 'Создание новых продуктов', 'products'),
    ('products:update', 'Обновление существующих продуктов', 'products'),
    ('products:delete', 'Удаление продуктов', 'products'),
    
    -- Права для зон
    ('zones:read', 'Просмотр информации о зонах склада', 'zones'),
    ('zones:create', 'Создание новых зон', 'zones'),
    ('zones:update', 'Обновление существующих зон', 'zones'),
    ('zones:delete', 'Удаление зон', 'zones'),
    
    -- Права для поставщиков
    ('suppliers:read', 'Просмотр информации о поставщиках', 'suppliers'),
    ('suppliers:create', 'Создание новых поставщиков', 'suppliers'),
    ('suppliers:update', 'Обновление существующих поставщиков', 'suppliers'),
    ('suppliers:delete', 'Удаление поставщиков', 'suppliers'),
    
    -- Права для QR-кодов
    ('qrcodes:generate', 'Генерация QR-кодов', 'qrcodes'),
    ('qrcodes:read', 'Просмотр QR-кодов', 'qrcodes');

-- Назначаем все разрешения роли ADMIN
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- Назначаем все разрешения роли MANAGER кроме admin:*
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MANAGER' AND p.category != 'admin';

-- Назначаем ограниченные разрешения для операторов (чтение + сканирование)
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'OPERATOR' AND (p.category = 'scan' OR p.name LIKE '%:read');

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

INSERT INTO users (username, password, role_id, account_non_expired, account_non_locked, credentials_non_expired, enabled) VALUES
    ('admin', '$2a$10$QQ7l4qHU2gGav/EvU7IjvO8sK4ArR6tyNv55KobDcZlBNzPmGr4pC',
    (SELECT id FROM roles WHERE name = 'ADMIN'), true, true, true, true),
    ('manager', '$2a$10$QQ7l4qHU2gGav/EvU7IjvO8sK4ArR6tyNv55KobDcZlBNzPmGr4pC',
    (SELECT id FROM roles WHERE name = 'MANAGER'), true, true, true, true),
    ('operator1', '$2a$10$QQ7l4qHU2gGav/EvU7IjvO8sK4ArR6tyNv55KobDcZlBNzPmGr4pC',
    (SELECT id FROM roles WHERE name = 'OPERATOR'), true, true, true, true),
    ('operator2', '$2a$10$QQ7l4qHU2gGav/EvU7IjvO8sK4ArR6tyNv55KobDcZlBNzPmGr4pC',
    (SELECT id FROM roles WHERE name = 'OPERATOR'), true, true, true, true);

-- Вставляем партии товаров
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
-- Здесь добавляем scanned_at и scanned_by (оператор1, id=3).
INSERT INTO boxes (code, product_batch_id, status, scanned_at, scanned_by) VALUES
                                                                               (NULL, 3, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 3),
                                                                               (NULL, 3, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '25 minutes', 3),
                                                                               (NULL, 3, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '20 minutes', 3),
                                                                               (NULL, 3, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '15 minutes', 3);

-- Партия 4 (id=4): 2 коробки, обе в состоянии GENERATED.
INSERT INTO boxes (code, product_batch_id, status) VALUES
                                                       (NULL, 4, 'GENERATED'),
                                                       (NULL, 4, 'GENERATED');

-- Партия 5 (id=5): 3 коробки, 2 в состоянии SCANNED, 1 в состоянии GENERATED.
-- Поэтому агрегированный статус партии остаётся STICKED.
INSERT INTO boxes (code, product_batch_id, status, scanned_at, scanned_by) VALUES
                                                                               (NULL, 5, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '40 minutes', 3),
                                                                               (NULL, 5, 'SCANNED', CURRENT_TIMESTAMP - INTERVAL '35 minutes', 3),
                                                                               (NULL, 5, 'GENERATED',NULL, NULL);

-- Вставляем события сканирования (примерные данные)
-- Предположим, что для партии 3 коробки (id 6,7,8,9) были отсканированы.
INSERT INTO scan_events (box_id, user_id, scan_mode, scan_time) VALUES
                                                                    ((SELECT id FROM boxes WHERE product_batch_id = 3 LIMIT 1), 3, 'ON_WAREHOUSE', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
                                                                    ((SELECT id FROM boxes WHERE product_batch_id = 3 ORDER BY id LIMIT 1 OFFSET 1), 3, 'ON_WAREHOUSE', CURRENT_TIMESTAMP - INTERVAL '25 minutes'),
                                                                    ((SELECT id FROM boxes WHERE product_batch_id = 3 ORDER BY id LIMIT 1 OFFSET 2), 3, 'ON_WAREHOUSE', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
                                                                    ((SELECT id FROM boxes WHERE product_batch_id = 3 ORDER BY id LIMIT 1 OFFSET 3), 3, 'ON_WAREHOUSE', CURRENT_TIMESTAMP - INTERVAL '15 minutes');
