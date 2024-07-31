CREATE TABLE IF NOT EXISTS transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) NOT NULL,
    amount DOUBLE NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    account_id VARCHAR(255) NOT NULL,
    merchant_id VARCHAR(255) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    confirmation_details VARCHAR(255)
);

CREATE TABLE  IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    role_id BIGINT NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    account_id VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    balance DOUBLE NOT NULL

);

INSERT INTO users (user_id,role_id, email, phone_number, account_id, username, password,balance) VALUES
('user1',2, 'user1@example.com', '+1234567891', 'account1', 'user1', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user2',1, 'user2@example.com', '+1234567892', 'account2', 'user2', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user3',1, 'user3@example.com', '+1234567893', 'account3', 'user3', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user4',1, 'user4@example.com', '+1234567894', 'account4', 'user4', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user5',1, 'user5@example.com', '+1234567895', 'account5', 'user5', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user6',1, 'user6@example.com', '+1234567896', 'account6', 'user6', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user7',1, 'user7@example.com', '+1234567897', 'account7', 'user7', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user8',1, 'user8@example.com', '+1234567898', 'account8', 'user8', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user9',1, 'user9@example.com', '+1234567899', 'account9', 'user9', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00),
('user10',1, 'user10@example.com', '+1234567800', 'account10', 'user10', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG',1000000.00);


CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

INSERT INTO roles (id, role_name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, role_name) VALUES (2, 'ROLE_ADMIN');
