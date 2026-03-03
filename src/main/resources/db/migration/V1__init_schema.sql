-- ========================
-- USERS TABLE
-- ========================
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL
);

-- ========================
-- WAREHOUSES TABLE
-- ========================
CREATE TABLE warehouses (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            location VARCHAR(200),
                            created_at TIMESTAMP DEFAULT NOW()
);

-- ========================
-- PRODUCTS TABLE
-- ========================
CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          sku VARCHAR(50) UNIQUE NOT NULL,
                          name VARCHAR(150) NOT NULL,
                          description TEXT,
                          unit_price NUMERIC(12,2),
                          category VARCHAR(80)
);

-- ========================
-- INVENTORY TABLE
-- ========================
CREATE TABLE inventory (
                           id SERIAL PRIMARY KEY,
                           product_id INT REFERENCES products(id),
                           warehouse_id INT REFERENCES warehouses(id),
                           quantity INT NOT NULL DEFAULT 0,
                           UNIQUE(product_id, warehouse_id)
);

-- ========================
-- STOCK MOVEMENTS TABLE
-- ========================
CREATE TABLE stock_movements (
                                 id SERIAL PRIMARY KEY,
                                 product_id INT REFERENCES products(id),
                                 warehouse_id INT REFERENCES warehouses(id),
                                 movement_type VARCHAR(20) NOT NULL,
                                 quantity INT NOT NULL,
                                 reference_note VARCHAR(255),
                                 moved_at TIMESTAMP DEFAULT NOW()
);

-- ========================
-- ORDERS TABLE
-- ========================
CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        order_number VARCHAR(50) UNIQUE NOT NULL,
                        status VARCHAR(30) DEFAULT 'PENDING',
                        created_by INT REFERENCES users(id),
                        created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id INT REFERENCES orders(id),
                             product_id INT REFERENCES products(id),
                             quantity INT NOT NULL,
                             unit_price NUMERIC(12,2)
);