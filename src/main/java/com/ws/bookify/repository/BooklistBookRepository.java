package com.ws.bookify.repository;

import com.ws.bookify.entity.BooklistBook;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooklistBookRepository extends JpaRepository<BooklistBook, Long> {

    /** หนังสือทั้งหมดใน list หนึ่ง เรียงตามลำดับ (position แล้วค่อย id) */
    List<BooklistBook> findByBooklistIdOrderByPositionAscIdAsc(Long booklistId);

    /** เช็คว่าหนังสือเล่มนี้อยู่ใน list นี้แล้วหรือยัง (กันเพิ่มซ้ำ) */
    boolean existsByBooklistIdAndBookId(Long booklistId, Long bookId);

    /** หา record เชื่อม เพื่อใช้ตอนลบหนังสือออกจาก list */
    Optional<BooklistBook> findByBooklistIdAndBookId(Long booklistId, Long bookId);

    /** นับจำนวนหนังสือใน list (ใช้คำนวณ position ถัดไป) */
    long countByBooklistId(Long booklistId);
}
