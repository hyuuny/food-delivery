CREATE TABLE IF NOT EXISTS menus
(
    id            SERIAL PRIMARY KEY,
    menu_group_id BIGINT                NOT NULL,
    name          VARCHAR(255)          NOT NULL,
    price         BIGINT                NOT NULL,
    status        VARCHAR(50)           NOT NULL,
    popularity    BOOLEAN DEFAULT FALSE NOT NULL,
    image_url     VARCHAR(255),
    description   VARCHAR(255),
    created_at    TIMESTAMP             NOT NULL,
    updated_at    TIMESTAMP             NOT NULL
);

CREATE TABLE IF NOT EXISTS option_groups
(
    id         SERIAL PRIMARY KEY,
    menu_id    BIGINT                NOT NULL,
    name       VARCHAR(255),
    required   BOOLEAN DEFAULT FALSE NOT NULL,
    priority   INT                   NOT NULL,
    created_at TIMESTAMP             NOT NULL,
    updated_at TIMESTAMP             NOT NULL
);

CREATE TABLE IF NOT EXISTS options
(
    id              SERIAL PRIMARY KEY,
    option_group_id BIGINT           NOT NULL,
    name            VARCHAR(255)     NOT NULL,
    price           BIGINT DEFAULT 0 NOT NULL,
    created_at      TIMESTAMP        NOT NULL,
    updated_at      TIMESTAMP        NOT NULL
);

CREATE TABLE IF NOT EXISTS menu_groups
(
    id          SERIAL PRIMARY KEY,
    store_id    BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    priority    INT          NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS stores
(
    id                   SERIAL PRIMARY KEY,
    category_id          BIGINT       NOT NULL,
    delivery_type        VARCHAR(50)  NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    owner_name           VARCHAR(255) NOT NULL,
    tax_id               VARCHAR(50)  NOT NULL,
    delivery_fee         BIGINT DEFAULT 0,
    minimum_order_amount BIGINT       NOT NULL,
    icon_image_url       VARCHAR(255),
    description          TEXT         NOT NULL,
    food_origin          TEXT         NOT NULL,
    phone_number         VARCHAR(20),
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS store_details
(
    id               SERIAL PRIMARY KEY,
    store_id         BIGINT       NOT NULL,
    zip_code         VARCHAR(10)  NOT NULL,
    address          VARCHAR(255) NOT NULL,
    detailed_address VARCHAR(255),
    open_hours       VARCHAR(255),
    closed_day       VARCHAR(255),
    created_at       TIMESTAMP    NOT NULL,

    FOREIGN KEY (store_id) REFERENCES stores (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS store_images
(
    id         SERIAL PRIMARY KEY,
    store_id   BIGINT       NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,

    FOREIGN KEY (store_id) REFERENCES stores (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categories
(
    id             SERIAL PRIMARY KEY,
    delivery_type  VARCHAR(50)          NOT NULL,
    name           VARCHAR(255)         NOT NULL,
    priority       INT                  NOT NULL,
    icon_image_url VARCHAR(255)         NOT NULL,
    visible        BOOLEAN DEFAULT TRUE NOT NULL,
    created_at     TIMESTAMP            NOT NULL,
    updated_at     TIMESTAMP            NOT NULL
);

CREATE TABLE IF NOT EXISTS carts
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_items
(
    id         SERIAL PRIMARY KEY,
    cart_id    BIGINT    NOT NULL,
    menu_id    BIGINT    NOT NULL,
    quantity   INT       NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts (id)
);

CREATE TABLE IF NOT EXISTS cart_item_options
(
    id           SERIAL PRIMARY KEY,
    cart_item_id BIGINT    NOT NULL,
    option_id    BIGINT    NOT NULL,
    created_at   TIMESTAMP NOT NULL,
    FOREIGN KEY (cart_item_id) REFERENCES cart_items (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(50)  NOT NULL,
    nickname     VARCHAR(50)  NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20)  NOT NULL,
    image_url    VARCHAR(255),
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_name ON users (name);
CREATE INDEX IF NOT EXISTS idx_users_phoneNumber ON users (phone_number);

CREATE TABLE IF NOT EXISTS user_addresses
(
    id                SERIAL PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    name              VARCHAR(255),
    zip_code          VARCHAR(255) NOT NULL,
    address           VARCHAR(255) NOT NULL,
    detail_address    VARCHAR(255) NOT NULL,
    message_to_rider  VARCHAR(255),
    entrance_password VARCHAR(255),
    route_guidance    VARCHAR(255),
    selected          BOOLEAN      NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_id ON user_addresses (user_id);

CREATE TABLE IF NOT EXISTS orders
(
    id               SERIAL PRIMARY KEY,
    order_number     VARCHAR(255) NOT NULL UNIQUE,
    user_id          BIGINT       NOT NULL,
    store_id         BIGINT       NOT NULL,
    category_id      BIGINT       NOT NULL,
    payment_id       VARCHAR(255) NOT NULL,
    payment_method   VARCHAR(50)  NOT NULL,
    status           VARCHAR(50)  NOT NULL,
    delivery_type    VARCHAR(50)  NOT NULL,
    zip_code         VARCHAR(20)  NOT NULL,
    address          VARCHAR(255) NOT NULL,
    detail_address   VARCHAR(255) NOT NULL,
    phone_number     VARCHAR(20)  NOT NULL,
    message_to_rider VARCHAR(255),
    message_to_store VARCHAR(255),
    total_price      BIGINT       NOT NULL,
    delivery_fee     BIGINT DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders (user_id);
CREATE INDEX IF NOT EXISTS idx_orders_store_id ON orders (store_id);
CREATE INDEX IF NOT EXISTS idx_orders_payment_id ON orders (payment_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders (status);

CREATE TABLE IF NOT EXISTS order_items
(
    id         SERIAL PRIMARY KEY,
    order_id   BIGINT       NOT NULL,
    menu_id    BIGINT       NOT NULL,
    menu_name  VARCHAR(255) NOT NULL,
    menu_price BIGINT       NOT NULL,
    quantity   INT          NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id)
);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items (order_id);

CREATE TABLE IF NOT EXISTS order_item_options
(
    id            SERIAL PRIMARY KEY,
    order_item_id BIGINT       NOT NULL,
    option_id     BIGINT       NOT NULL,
    option_name   VARCHAR(255) NOT NULL,
    option_price  BIGINT       NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    FOREIGN KEY (order_item_id) REFERENCES order_items (id)
);
CREATE INDEX IF NOT EXISTS idx_order_item_options_order_item_id ON order_item_options (order_item_id);

CREATE TABLE IF NOT EXISTS reviews
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    store_id   BIGINT    NOT NULL,
    order_id   BIGINT    NOT NULL,
    score      INT       NOT NULL,
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews (user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_store_id ON reviews (store_id);

CREATE TABLE IF NOT EXISTS review_photos
(
    id         SERIAL PRIMARY KEY,
    review_id  BIGINT    NOT NULL,
    photo_url  TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (review_id) REFERENCES reviews (id)
);
CREATE INDEX IF NOT EXISTS idx_review_photos_review_id ON review_photos (review_id);

