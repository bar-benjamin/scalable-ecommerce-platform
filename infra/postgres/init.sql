-- user-service
CREATE SCHEMA IF NOT EXISTS user_schema;

-- product-service
CREATE SCHEMA IF NOT EXISTS product_schema;

-- cart-service
CREATE SCHEMA IF NOT EXISTS cart_schema;

-- order-service
CREATE SCHEMA IF NOT EXISTS order_schema;

-- payment-service
CREATE SCHEMA IF NOT EXISTS payment_schema;

-- notification-service
CREATE SCHEMA IF NOT EXISTS notification_schema;

GRANT ALL PRIVILEGES ON SCHEMA user_schema         TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA product_schema      TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA cart_schema         TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA order_schema        TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA payment_schema      TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA notification_schema TO postgres;
