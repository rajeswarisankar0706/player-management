CREATE TABLE IF NOT EXISTS player (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    address VARCHAR(255) NOT NULL,
    daily_time_limit_in_seconds BIGINT
);

CREATE TABLE IF NOT EXISTS session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT,
    login_time TIMESTAMP NOT NULL,
    logout_time TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES player(id)
);
