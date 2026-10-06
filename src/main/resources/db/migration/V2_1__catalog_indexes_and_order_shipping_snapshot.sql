SET @has_products_created_at = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'products'
      AND column_name = 'created_at'
);
SET @add_products_created_at = IF(
    @has_products_created_at = 0,
    'ALTER TABLE products ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP',
    'SELECT 1'
);
PREPARE add_products_created_at FROM @add_products_created_at;
EXECUTE add_products_created_at;
DEALLOCATE PREPARE add_products_created_at;

SET @has_shipping_street = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'orders'
      AND column_name = 'shipping_street'
);
SET @add_shipping_street = IF(
    @has_shipping_street = 0,
    'ALTER TABLE orders ADD COLUMN shipping_street VARCHAR(255)',
    'SELECT 1'
);
PREPARE add_shipping_street FROM @add_shipping_street;
EXECUTE add_shipping_street;
DEALLOCATE PREPARE add_shipping_street;

SET @has_shipping_city = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'orders'
      AND column_name = 'shipping_city'
);
SET @add_shipping_city = IF(
    @has_shipping_city = 0,
    'ALTER TABLE orders ADD COLUMN shipping_city VARCHAR(100)',
    'SELECT 1'
);
PREPARE add_shipping_city FROM @add_shipping_city;
EXECUTE add_shipping_city;
DEALLOCATE PREPARE add_shipping_city;

SET @has_shipping_state = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'orders'
      AND column_name = 'shipping_state'
);
SET @add_shipping_state = IF(
    @has_shipping_state = 0,
    'ALTER TABLE orders ADD COLUMN shipping_state VARCHAR(100)',
    'SELECT 1'
);
PREPARE add_shipping_state FROM @add_shipping_state;
EXECUTE add_shipping_state;
DEALLOCATE PREPARE add_shipping_state;

SET @has_shipping_postal_code = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'orders'
      AND column_name = 'shipping_postal_code'
);
SET @add_shipping_postal_code = IF(
    @has_shipping_postal_code = 0,
    'ALTER TABLE orders ADD COLUMN shipping_postal_code VARCHAR(20)',
    'SELECT 1'
);
PREPARE add_shipping_postal_code FROM @add_shipping_postal_code;
EXECUTE add_shipping_postal_code;
DEALLOCATE PREPARE add_shipping_postal_code;

SET @has_shipping_country = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'orders'
      AND column_name = 'shipping_country'
);
SET @add_shipping_country = IF(
    @has_shipping_country = 0,
    'ALTER TABLE orders ADD COLUMN shipping_country VARCHAR(100)',
    'SELECT 1'
);
PREPARE add_shipping_country FROM @add_shipping_country;
EXECUTE add_shipping_country;
DEALLOCATE PREPARE add_shipping_country;

ALTER TABLE products
    DROP INDEX idx_products_category_id,
    ADD INDEX idx_products_category_price (category_id, price),
    ADD INDEX idx_products_price (price),
    ADD INDEX idx_products_name (name),
    ADD INDEX idx_products_created_at (created_at);

UPDATE orders existing_order
JOIN addresses saved_address ON saved_address.id = existing_order.shipping_address_id
SET existing_order.shipping_street = saved_address.street,
    existing_order.shipping_city = saved_address.city,
    existing_order.shipping_state = saved_address.state,
    existing_order.shipping_postal_code = saved_address.postal_code,
    existing_order.shipping_country = saved_address.country;

ALTER TABLE orders
    MODIFY COLUMN shipping_street VARCHAR(255) NOT NULL,
    MODIFY COLUMN shipping_city VARCHAR(100) NOT NULL,
    MODIFY COLUMN shipping_postal_code VARCHAR(20) NOT NULL,
    MODIFY COLUMN shipping_country VARCHAR(100) NOT NULL,
    DROP FOREIGN KEY fk_orders_shipping_address,
    DROP INDEX idx_orders_shipping_address_id,
    DROP COLUMN shipping_address_id;