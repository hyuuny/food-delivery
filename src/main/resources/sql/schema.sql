CREATE TABLE IF NOT EXISTS menus
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)          NOT NULL,
    price       BIGINT                NOT NULL,
    status      VARCHAR(50)           NOT NULL,
    popularity  BOOLEAN DEFAULT FALSE NOT NULL,
    image_url   VARCHAR(255),
    description VARCHAR(255),
    created_at  TIMESTAMP             NOT NULL,
    updated_at  TIMESTAMP             NOT NULL
);

CREATE TABLE IF NOT EXISTS option_group
(
    id         SERIAL PRIMARY KEY,
    menu_id    BIGINT                NOT NULL,
    name       VARCHAR(255),
    required   BOOLEAN DEFAULT FALSE NOT NULL,
    priority   INT                   NOT NULL,
    created_at TIMESTAMP             NOT NULL,
    updated_at TIMESTAMP             NOT NULL
);

CREATE TABLE IF NOT EXISTS option
(
    id              SERIAL PRIMARY KEY,
    option_group_id BIGINT           NOT NULL,
    name            VARCHAR(255)     NOT NULL,
    price           BIGINT DEFAULT 0 NOT NULL,
    created_at      TIMESTAMP        NOT NULL,
    updated_at      TIMESTAMP        NOT NULL
);

CREATE TABLE IF NOT EXISTS menu_group
(
    id          SERIAL PRIMARY KEY,
    store_id    BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    priority    INT          NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS store
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
    phone_number         VARCHAR(255),
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS store_detail
(
    id               SERIAL PRIMARY KEY,
    store_id         BIGINT       NOT NULL,
    zip_code         VARCHAR(10)  NOT NULL,
    address          VARCHAR(255) NOT NULL,
    detailed_address VARCHAR(255),
    open_hours       VARCHAR(255),
    closed_day       VARCHAR(255),
    created_at       TIMESTAMP    NOT NULL,

    FOREIGN KEY (store_id) REFERENCES store (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS store_image
(
    id         SERIAL PRIMARY KEY,
    store_id   BIGINT       NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,

    FOREIGN KEY (store_id) REFERENCES store (id) ON DELETE CASCADE
);

