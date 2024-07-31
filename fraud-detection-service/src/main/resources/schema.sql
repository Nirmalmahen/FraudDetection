CREATE TABLE users (
    user_id VARCHAR(255) PRIMARY KEY,
    role_id BIGINT NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    account_id VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL

);

INSERT INTO users (user_id,role_id, email, phone_number, account_id, username, password) VALUES
('user1',2, 'user1@example.com', '+1234567891', 'account1', 'user1', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user2',1, 'user2@example.com', '+1234567892', 'account2', 'user2', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user3',1, 'user3@example.com', '+1234567893', 'account3', 'user3', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user4',1, 'user4@example.com', '+1234567894', 'account4', 'user4', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user5',1, 'user5@example.com', '+1234567895', 'account5', 'user5', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user6',1, 'user6@example.com', '+1234567896', 'account6', 'user6', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user7',1, 'user7@example.com', '+1234567897', 'account7', 'user7', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user8',1, 'user8@example.com', '+1234567898', 'account8', 'user8', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user9',1, 'user9@example.com', '+1234567899', 'account9', 'user9', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG'),
('user10',1, 'user10@example.com', '+1234567800', 'account10', 'user10', '$2a$10$n6v4cuONMfX3jz01x3KRc.AMzib5d5tFJlR5UOJksBDnmp9ADV0jG');


CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

INSERT INTO roles (id, role_name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, role_name) VALUES (2, 'ROLE_ADMIN');



CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    merchant VARCHAR(100) NOT NULL,
    transaction_date TIMESTAMP NOT NULL
);

INSERT INTO transactions (account_id, amount, merchant, transaction_date) VALUES ('account1', 200.50, 'MerchantA', '2024-07-22 12:00:00');
INSERT INTO transactions (account_id, amount, merchant, transaction_date) VALUES ('account2', 1000.00, 'MerchantB', '2024-07-22 13:00:00');
INSERT INTO transactions (account_id, amount, merchant, transaction_date) VALUES ('account3', 150.75, 'MerchantC', '2024-07-22 14:00:00');
INSERT INTO transactions (account_id, amount, merchant, transaction_date) VALUES ('account4', 50.00, 'MerchantD', '2024-07-22 15:00:00');
INSERT INTO transactions (account_id, amount, merchant, transaction_date) VALUES ('account5', 300.00, 'MerchantE', '2024-07-22 16:00:00');