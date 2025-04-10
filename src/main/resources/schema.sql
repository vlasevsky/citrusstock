-- Drop tables in обратном порядке (учитывая внешние ключи)
DROP TABLE IF EXISTS scan_events;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS roles_permissions;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS boxes;
DROP TABLE IF EXISTS product_batches;
DROP TABLE IF EXISTS zones;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- Roles table (роли пользователей)
CREATE TABLE roles (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(50) NOT NULL UNIQUE,
                      description VARCHAR(255)
);

-- Permissions table (разрешения)
CREATE TABLE permissions (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL UNIQUE,
                         description VARCHAR(255)
);

-- Roles-Permissions mapping table (связь роли-разрешения)
CREATE TABLE roles_permissions (
                            role_id BIGINT NOT NULL,
                            permission_id BIGINT NOT NULL,
                            PRIMARY KEY (role_id, permission_id),
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                            FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Products table
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL
);

-- Suppliers table
CREATE TABLE suppliers (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL
);

-- Zones table (динамические зоны, цвет в формате HEX, например, "#FFFFFF")
CREATE TABLE zones (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       color VARCHAR(7) NOT NULL
);

-- Product batches table
CREATE TABLE product_batches (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id BIGINT,
                                 supplier_id BIGINT,
                                 received_at TIMESTAMP NOT NULL,
                                 status VARCHAR(50) NOT NULL,  -- unified GoodsStatus: GENERATED, STICKED, SCANNED, SHIPPED
                                 zone_id BIGINT,             -- связь с динамической зоной
                                 FOREIGN KEY (product_id) REFERENCES products(id),
                                 FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
                                 FOREIGN KEY (zone_id) REFERENCES zones(id)
);

-- Boxes table
CREATE TABLE boxes (
                       id BIGSERIAL PRIMARY KEY,
                       code TEXT,  -- хранится Base64-строка
                       product_batch_id BIGINT NOT NULL,
                       status VARCHAR(50),  -- unified GoodsStatus
                       scanned_at TIMESTAMP,
                       scanned_by BIGINT,
                       FOREIGN KEY (product_batch_id) REFERENCES product_batches(id)
);

-- Users table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role_id BIGINT NOT NULL,
                       account_non_expired BOOLEAN DEFAULT TRUE,
                       account_non_locked BOOLEAN DEFAULT TRUE,
                       credentials_non_expired BOOLEAN DEFAULT TRUE,
                       enabled BOOLEAN DEFAULT TRUE,
                       FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Refresh tokens table
CREATE TABLE refresh_tokens (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           token VARCHAR(255) NOT NULL,
                           expiry_date TIMESTAMP NOT NULL,
                           issued_at TIMESTAMP NOT NULL,
                           revoked BOOLEAN DEFAULT FALSE,
                           device_info VARCHAR(255),
                           ip_address VARCHAR(45),
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Scan events table
CREATE TABLE scan_events (
                             id BIGSERIAL PRIMARY KEY,
                             box_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             scan_mode VARCHAR(50) NOT NULL,
                             scan_time TIMESTAMP NOT NULL,
                             FOREIGN KEY (box_id) REFERENCES boxes(id),
                             FOREIGN KEY (user_id) REFERENCES users(id)
);
