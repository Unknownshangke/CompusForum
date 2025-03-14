CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    avatar VARCHAR(255),
    is_admin BOOLEAN DEFAULT FALSE,
    is_moderator BOOLEAN DEFAULT FALSE,
    introduction TEXT,
    created_date DATETIME NOT NULL
);

CREATE TABLE user_moderator_permissions (
    user_id BIGINT NOT NULL,
    section VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, section),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
