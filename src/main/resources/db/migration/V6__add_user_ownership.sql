-- Flyway V6 — เพิ่มเจ้าของ (user_id) ให้ books / booklists / readings
-- แต่ละ user เห็น/แก้ได้เฉพาะข้อมูลของตัวเอง. ลบ user -> ลบข้อมูลทั้งหมด (CASCADE).
--
-- ข้อมูลเดิม (ก่อนมีระบบ user) ไม่มีเจ้าของ จึงล้างทิ้งก่อน เพื่อให้เพิ่ม
-- คอลัมน์ user_id แบบ NOT NULL ได้ (ตัดสินใจร่วมกับเจ้าของโปรเจกต์ — ยอมรับการสูญเสียข้อมูลชุดนี้).
TRUNCATE TABLE booklist_books, readings, books, booklists RESTART IDENTITY CASCADE;

ALTER TABLE books
    ADD COLUMN user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE;
CREATE INDEX idx_books_user_id ON books (user_id);

ALTER TABLE booklists
    ADD COLUMN user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE;
CREATE INDEX idx_booklists_user_id ON booklists (user_id);

ALTER TABLE readings
    ADD COLUMN user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE;
CREATE INDEX idx_readings_user_id ON readings (user_id);
