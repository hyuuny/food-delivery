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
