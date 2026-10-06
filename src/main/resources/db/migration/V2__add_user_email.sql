ALTER TABLE users
    ADD COLUMN email VARCHAR(255);

UPDATE users
SET email = username
WHERE email IS NULL;

ALTER TABLE users
    MODIFY COLUMN email VARCHAR(255) NOT NULL,
    ADD CONSTRAINT uk_users_email UNIQUE (email);