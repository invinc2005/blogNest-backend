CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_name VARCHAR(100),
    author_profile_pic_url VARCHAR(255),
    like_count INT DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME
);
