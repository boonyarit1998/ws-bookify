-- Flyway V4 — บันทึกเวลาที่อ่านจบ เพื่อทำสถิติ "อ่านจบต่อปี" ได้แม่นยำ
-- (set ตอน status เปลี่ยนเป็น FINISHED ครั้งแรก, ล้างเป็น null ถ้าเปลี่ยนกลับ)

ALTER TABLE readings ADD COLUMN finished_at TIMESTAMPTZ;

CREATE INDEX idx_readings_finished_at ON readings (finished_at);
