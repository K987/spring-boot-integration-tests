-- Flyway migration script to create t_users table for User entity (MySQL version)

CREATE TABLE t_users (
    id BINARY(16) DEFAULT (UUID()) PRIMARY KEY,
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(255),
    user_status INTEGER
);
