-- Flyway migration script to insert 2 users into t_users
INSERT INTO t_users (id, username, first_name, last_name, email, password, phone, user_status)
VALUES (nextval('t_users_seq'), 'jdoe', 'John', 'Doe', 'jdoe@example.com', 'password1', '1234567890', 1);

INSERT INTO t_users (id, username, first_name, last_name, email, password, phone, user_status)
VALUES (nextval('t_users_seq'), 'asmith', 'Alice', 'Smith', 'asmith@example.com', 'password2', '0987654321', 1);

