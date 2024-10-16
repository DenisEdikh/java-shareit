drop table if exists items cascade;
drop table if exists users cascade;
drop table if exists bookings cascade;
drop table if exists comments cascade;
drop table if exists requests cascade;

CREATE TABLE IF NOT EXISTS users
(
    id    bigint      NOT NULL GENERATED ALWAYS AS IDENTITY,
    name  varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           bigint                      NOT NULL GENERATED ALWAYS AS IDENTITY,
    description  text                        NOT NULL,
    user_id      bigint                      NOT NULL,
    created_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_user_request FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS items
(
    id          bigint       NOT NULL GENERATED ALWAYS AS IDENTITY,
    name        varchar(50)  NOT NULL,
    description varchar(250) NOT NULL,
    user_id     bigint       NOT NULL,
    request_id  bigint NULL,
    available   boolean      NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE SET NULL,
    CONSTRAINT fk_user_item FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         bigint      NOT NULL GENERATED ALWAYS AS IDENTITY,
    start_time timestamp   NOT NULL,
    end_time   timestamp   NOT NULL,
    item_id    bigint      NOT NULL,
    user_id    bigint      NOT NULL,
    status     varchar(10) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_item_booking FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_booking FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id           bigint                      NOT NULL GENERATED ALWAYS AS IDENTITY,
    description  text                        NOT NULL,
    item_id      bigint                      NOT NULL,
    user_id      bigint                      NOT NULL,
    created_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_item_comment FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_comment FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
