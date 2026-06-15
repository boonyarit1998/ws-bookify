-- Flyway V2 — ตารางเชื่อม book <-> booklist (Many-to-Many ผ่าน join entity)
-- เก็บข้อมูลเพิ่มของความสัมพันธ์: ลำดับใน list (position) และเวลาเพิ่ม (added_at)

CREATE TABLE booklist_books (
    id          BIGSERIAL PRIMARY KEY,
    booklist_id BIGINT NOT NULL REFERENCES booklists(id) ON DELETE CASCADE,
    book_id     BIGINT NOT NULL REFERENCES books(id)     ON DELETE CASCADE,
    position    INT,
    added_at    TIMESTAMPTZ,
    -- กันไม่ให้เพิ่มหนังสือเล่มเดิมซ้ำใน list เดียวกัน
    CONSTRAINT uq_booklist_book UNIQUE (booklist_id, book_id)
);

-- index ช่วยตอน query หนังสือทั้งหมดใน list หนึ่งๆ
CREATE INDEX idx_booklist_books_booklist_id ON booklist_books (booklist_id);
