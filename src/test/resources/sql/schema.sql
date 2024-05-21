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

CREATE TABLE IF NOT EXISTS menu_group
(
    id         SERIAL PRIMARY KEY,
    menu_id    BIGINT                NOT NULL,
    name       VARCHAR(255),
    required   BOOLEAN DEFAULT FALSE NOT NULL,
    priority   INT                   NOT NULL,
    created_at TIMESTAMP             NOT NULL,
    updated_at TIMESTAMP             NOT NULL
);