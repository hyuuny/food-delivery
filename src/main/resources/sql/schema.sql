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
CREATE INDEX IF NOT EXISTS idx_menus_menu_group_id ON menus (menu_group_id);

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
CREATE INDEX IF NOT EXISTS idx_option_groups_menu_id ON option_groups (menu_id);

CREATE TABLE IF NOT EXISTS options
(
    id              SERIAL PRIMARY KEY,
    option_group_id BIGINT           NOT NULL,
    name            VARCHAR(255)     NOT NULL,
    price           BIGINT DEFAULT 0 NOT NULL,
    created_at      TIMESTAMP        NOT NULL,
    updated_at      TIMESTAMP        NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_options_option_group_id ON options (option_group_id);

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
CREATE INDEX IF NOT EXISTS idx_menu_groups_store_id ON menu_groups (store_id);

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
CREATE INDEX IF NOT EXISTS idx_stores_category_id ON stores (category_id);

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
CREATE INDEX IF NOT EXISTS idx_store_details_store_id ON store_details (store_id);

CREATE TABLE IF NOT EXISTS store_images
(
    id         SERIAL PRIMARY KEY,
    store_id   BIGINT       NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,

    FOREIGN KEY (store_id) REFERENCES stores (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_store_images_store_id ON store_images (store_id);

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
    id           SERIAL PRIMARY KEY,
    user_id      BIGINT    NOT NULL,
    store_id     BIGINT,
    delivery_fee BIGINT DEFAULT 0,
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_user_id ON carts (user_id);

CREATE TABLE IF NOT EXISTS cart_items
(
    id         SERIAL PRIMARY KEY,
    cart_id    BIGINT    NOT NULL,
    menu_id    BIGINT    NOT NULL,
    quantity   INT       NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cart_item_options
(
    id           SERIAL PRIMARY KEY,
    cart_item_id BIGINT    NOT NULL,
    option_id    BIGINT    NOT NULL,
    created_at   TIMESTAMP NOT NULL,

    FOREIGN KEY (cart_item_id) REFERENCES cart_items (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    id           SERIAL PRIMARY KEY,
    user_type    VARCHAR(50)  NOT NULL,
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
    updated_at        TIMESTAMP    NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses (user_id);

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

CREATE TABLE IF NOT EXISTS review_comments
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    review_id  BIGINT    NOT NULL,
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_review_comments_review_id ON review_comments (review_id);

CREATE TABLE IF NOT EXISTS liked_store
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    store_id   BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT unique_user_store UNIQUE (user_id, store_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_store FOREIGN KEY (store_id) REFERENCES stores (id)
);
CREATE INDEX IF NOT EXISTS idx_liked_stores_user_id ON liked_store (user_id);
CREATE INDEX IF NOT EXISTS idx_liked_stores_user_id_store_id ON liked_store (user_id, store_id);

CREATE TABLE IF NOT EXISTS deliveries
(
    id             SERIAL PRIMARY KEY,
    rider_id       BIGINT      NOT NULL,
    order_id       BIGINT      NOT NULL,
    status         VARCHAR(20) NOT NULL,
    pickup_time    TIMESTAMP,
    delivered_time TIMESTAMP,
    cancel_time    TIMESTAMP,
    created_at     TIMESTAMP   NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_deliveries_rider_id ON deliveries (rider_id);

CREATE TABLE IF NOT EXISTS coupons
(
    id                   SERIAL PRIMARY KEY,
    code                 VARCHAR(255) NOT NULL,
    type                 VARCHAR(255) NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    discount_amount      BIGINT       NOT NULL,
    minimum_order_amount BIGINT       NOT NULL,
    description          TEXT         NOT NULL,
    issue_start_date     TIMESTAMP    NOT NULL,
    issue_end_date       TIMESTAMP    NOT NULL,
    valid_from           TIMESTAMP    NOT NULL,
    valid_to             TIMESTAMP    NOT NULL,
    created_at           TIMESTAMP    NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_coupons_code ON coupons (code);

CREATE TABLE IF NOT EXISTS user_coupons
(
    id          SERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL,
    coupon_id   BIGINT    NOT NULL,
    used        BOOLEAN DEFAULT FALSE,
    used_date   TIMESTAMP,
    issued_date TIMESTAMP NOT NULL,
    FOREIGN KEY (coupon_id) REFERENCES coupons (id)
);

CREATE INDEX IF NOT EXISTS idx_user_coupons_user_id ON user_coupons (user_id);
CREATE INDEX IF NOT EXISTS idx_user_coupons_coupon_id ON user_coupons (coupon_id);

