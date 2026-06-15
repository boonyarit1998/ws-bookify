package com.ws.bookify.repository;

import com.ws.bookify.entity.Reading;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {

    /** หา reading record ของหนังสือเล่มหนึ่ง เฉพาะที่เป็นของ user คนนี้ */
    Optional<Reading> findByBookIdAndUserId(Long bookId, Long userId);

    // ---- queries สำหรับสถิติ (scope เฉพาะ reading ของ user คนนี้) ----

    /** นับจำนวนแยกตาม status -> แต่ละแถวเป็น [ReadingStatus, Long] */
    @Query("SELECT r.status, COUNT(r) FROM Reading r WHERE r.userId = :userId GROUP BY r.status")
    List<Object[]> countGroupByStatus(@Param("userId") Long userId);

    /** คะแนนเฉลี่ย (เฉพาะที่มี rating) -> null ถ้ายังไม่มีใครให้คะแนน */
    @Query("SELECT AVG(r.rating) FROM Reading r WHERE r.userId = :userId AND r.rating IS NOT NULL")
    Double averageRating(@Param("userId") Long userId);

    /** จำนวนรีวิวที่มีข้อความ */
    @Query("SELECT COUNT(r) FROM Reading r WHERE r.userId = :userId AND r.review IS NOT NULL AND r.review <> ''")
    long countReviews(@Param("userId") Long userId);

    /** จำนวนหนังสือที่อ่านจบ แยกตามปี -> แต่ละแถวเป็น [year, count] เรียงตามปี */
    @Query(value = """
            SELECT EXTRACT(YEAR FROM finished_at)::int AS yr, COUNT(*) AS cnt
            FROM readings
            WHERE user_id = :userId AND finished_at IS NOT NULL
            GROUP BY yr
            ORDER BY yr
            """, nativeQuery = true)
    List<Object[]> countFinishedPerYear(@Param("userId") Long userId);
}
