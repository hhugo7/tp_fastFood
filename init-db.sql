-- Script d'initialisation de la base de données FoodFast
-- Crée les tables nécessaires pour stocker les commandes

-- Table des commandes
CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table de liaison entre les commandes et les plats (avec quantité)
CREATE TABLE IF NOT EXISTS order_dishes (
    id SERIAL PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date);
CREATE INDEX IF NOT EXISTS idx_order_dishes_order_id ON order_dishes(order_id);
