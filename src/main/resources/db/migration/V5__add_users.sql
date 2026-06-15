-- Flyway V5 — ตาราง users สำหรับ authentication (register/login)
-- เก็บทั้ง username และ email (unique ทั้งคู่); login ใช้ email + password.

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,   -- BCrypt hash (ไม่เก็บ password ดิบ)
    created_at    TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ
);
