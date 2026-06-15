-- Flyway V3 — สถานะการอ่าน + รีวิว ต่อหนังสือ 1 เล่ม (1 book : 1 reading)

CREATE TABLE readings (
    id           BIGSERIAL PRIMARY KEY,
    book_id      BIGINT NOT NULL UNIQUE REFERENCES books(id) ON DELETE CASCADE,
    status       VARCHAR(20) NOT NULL,
    rating       INT,
    review       TEXT,
    current_page INT,
    created_at   TIMESTAMPTZ,
    updated_at   TIMESTAMPTZ,
    -- rating ต้องอยู่ระหว่าง 1-5 (หรือเป็น null)
    CONSTRAINT chk_reading_rating CHECK (rating IS NULL OR rating BETWEEN 1 AND 5)
);
