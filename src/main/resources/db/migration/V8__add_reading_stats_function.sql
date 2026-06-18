-- Flyway V8 — stored function รวมสถิติการอ่านของ user คนเดียวให้จบใน round-trip เดียว
--
-- เดิม ReadingStatsService ยิง 4 query แยกกัน (นับตาม status / คะแนนเฉลี่ย / จำนวนรีวิว /
-- อ่านจบต่อปี) แล้วค่อยประกอบผลใน Java. ฟังก์ชันนี้ยุบให้เหลือ query เดียว คืนผลเป็น JSONB
-- ที่ map ตรงกับ ReadingStatsResponse (ฝั่งแอปให้ Gson แปลงกลับเป็น record ได้เลย).
--
-- หมายเหตุ: เลือกเป็น FUNCTION ไม่ใช่ PROCEDURE เพราะต้อง "คืนค่า" — PROCEDURE ของ Postgres
-- เรียกด้วย CALL และคืนค่าผ่าน OUT param เท่านั้น ซึ่งไม่สะดวกกับ SELECT ของ JPA.
-- ทำเป็น SQL function (ไม่ใช่ PL/pgSQL) เพราะ logic ทั้งหมดเป็น aggregate ล้วน ๆ —
-- ปล่อยให้ planner inline + optimize ได้เต็มที่ และอ่านง่ายกว่า.

CREATE OR REPLACE FUNCTION fn_user_reading_stats(p_user_id BIGINT)
RETURNS JSONB
LANGUAGE sql
STABLE
AS $$
    SELECT jsonb_build_object(
        -- จำนวน reading record ทั้งหมดของ user คนนี้
        'totalReadings',
            (SELECT COUNT(*) FROM readings WHERE user_id = p_user_id),

        -- นับแยกตาม status — ใช้ VALUES list เป็นแกนหลักเพื่อให้ output มีครบทุกสถานะเสมอ
        -- (เป็น 0 ถ้ายังไม่มี) และ ORDER BY rank ให้ลำดับ key คงที่ตามลำดับใน enum
        'byStatus', (
            SELECT jsonb_object_agg(s.status, COALESCE(c.cnt, 0) ORDER BY s.rank)
            FROM (VALUES
                ('WANT_TO_READ', 1),
                ('READING', 2),
                ('FINISHED', 3)
            ) AS s(status, rank)
            LEFT JOIN (
                SELECT status, COUNT(*) AS cnt
                FROM readings
                WHERE user_id = p_user_id
                GROUP BY status
            ) c ON c.status = s.status
        ),

        -- คะแนนเฉลี่ย ปัดเหลือ 2 ตำแหน่ง, 0 ถ้ายังไม่มีใครให้คะแนน
        'averageRating', (
            SELECT COALESCE(ROUND(AVG(rating)::numeric, 2), 0)
            FROM readings
            WHERE user_id = p_user_id AND rating IS NOT NULL
        ),

        -- จำนวนรีวิวที่มีข้อความ (ไม่ใช่ null และไม่ใช่สตริงว่าง)
        'totalReviews', (
            SELECT COUNT(*)
            FROM readings
            WHERE user_id = p_user_id AND review IS NOT NULL AND review <> ''
        ),

        -- อ่านจบกี่เล่ม แยกตามปี เรียงตามปี; คืน [] ถ้ายังไม่มีเล่มที่อ่านจบ
        'finishedPerYear', (
            SELECT COALESCE(
                jsonb_agg(jsonb_build_object('year', t.yr, 'count', t.cnt) ORDER BY t.yr),
                '[]'::jsonb
            )
            FROM (
                SELECT EXTRACT(YEAR FROM finished_at)::int AS yr, COUNT(*) AS cnt
                FROM readings
                WHERE user_id = p_user_id AND finished_at IS NOT NULL
                GROUP BY 1
            ) t
        )
    );
$$;

COMMENT ON FUNCTION fn_user_reading_stats(BIGINT) IS
    'รวมสถิติการอ่านของ user คนเดียวเป็น JSONB (map ตรงกับ ReadingStatsResponse) ใน query เดียว';
