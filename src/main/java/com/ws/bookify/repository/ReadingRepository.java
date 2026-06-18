package com.ws.bookify.repository;

import com.ws.bookify.entity.Reading;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {

    /** หา reading record ของหนังสือเล่มหนึ่ง เฉพาะที่เป็นของ user คนนี้ */
    Optional<Reading> findByBookIdAndUserId(Long bookId, Long userId);

    /**
     * รวมสถิติการอ่านทั้งหมดของ user คนนี้ใน round-trip เดียว ผ่าน stored function
     * fn_user_reading_stats (ดู Flyway V8). คืนเป็น JSON string ที่ map ตรงกับ
     * ReadingStatsResponse — cast ::text เพื่อให้ JDBC คืนค่าเป็น String (ไม่ใช่ PGobject).
     */
    @Query(value = "SELECT fn_user_reading_stats(:userId)::text", nativeQuery = true)
    String readingStats(@Param("userId") Long userId);
}
