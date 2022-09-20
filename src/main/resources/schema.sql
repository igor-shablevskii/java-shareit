-- drop table if exists bookings;
-- drop table if exists comments;
-- drop table if exists items;
-- drop table if exists users;

CREATE TABLE IF NOT EXISTS users
(
    id    bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL
        CONSTRAINT pk_users PRIMARY KEY,
    name  varchar                                 NOT NULL,
    email varchar UNIQUE                          NOT NULL,
    CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id           bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL
        CONSTRAINT pk_item PRIMARY KEY,
    name         varchar                                 NOT NULL,
    description  varchar                                 NOT NULL,
    is_available boolean                                 NOT NULL,
    owner_id     bigint                                  NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL
        CONSTRAINT pk_booking PRIMARY KEY,
    start_date timestamp                               NOT NULL,
    end_date   timestamp                               NOT NULL,
    item_id    bigint                                  NOT NULL,
    booker_id  bigint                                  NOT NULL,
    status     varchar                                 NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    id        bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL
        CONSTRAINT pk_comment PRIMARY KEY,
    TEXT      varchar                                 NOT NULL,
    item_id   bigint                                  NOT NULL,
    author_id bigint                                  NOT NULL,
    created   timestamp                               NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);
