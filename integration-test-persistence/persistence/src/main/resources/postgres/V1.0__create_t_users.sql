-- Flyway migration script to create t_users table for User entity (PostgreSQL version)

CREATE TABLE t_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(255),
    user_status INTEGER
);