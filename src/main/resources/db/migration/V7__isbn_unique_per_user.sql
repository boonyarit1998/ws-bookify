-- Flyway V7 — เปลี่ยน ISBN จาก unique ทั้งระบบ เป็น unique เฉพาะต่อ user
-- เดิม V1 ตั้ง isbn UNIQUE (ทั้งตาราง) -> Postgres ตั้งชื่อ constraint อัตโนมัติเป็น books_isbn_key
-- ตอนนี้แต่ละ user มี library ของตัวเอง จึงให้ ISBN ซ้ำข้าม user ได้ แต่ห้ามซ้ำภายใน user เดียวกัน
--
-- หมายเหตุ: isbn ที่เป็น NULL จะไม่ถูกบังคับ unique (Postgres ถือว่า NULL ต่างกันเสมอ)
-- => user หนึ่งมีหนังสือที่ไม่มี ISBN ได้หลายเล่ม

ALTER TABLE books DROP CONSTRAINT IF EXISTS books_isbn_key;

ALTER TABLE books ADD CONSTRAINT uq_books_user_isbn UNIQUE (user_id, isbn);
