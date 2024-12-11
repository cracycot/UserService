CREATE TABLE Users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    lastname VARCHAR(50),
    email VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20),
    birthday DATE
)