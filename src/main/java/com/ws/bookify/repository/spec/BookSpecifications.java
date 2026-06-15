package com.ws.bookify.repository.spec;

import com.ws.bookify.entity.Book;
import com.ws.bookify.entity.Reading;
import com.ws.bookify.entity.ReadingStatus;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

/**
 * รวม Specification สำหรับ search/filter หนังสือ.
 * แต่ละ method คืนเงื่อนไข 1 ข้อ ที่นำไปประกอบกัน (AND) ได้ตามต้องการ.
 */
public final class BookSpecifications {

    private BookSpecifications() {
    }

    /** เฉพาะหนังสือของ user คนนี้ (scope ownership — ใส่เป็นเงื่อนไขแรกของ search) */
    public static Specification<Book> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    /** title มีคำว่า ... (ไม่สนตัวพิมพ์เล็ก-ใหญ่) */
    public static Specification<Book> titleContains(String title) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    /** author มีคำว่า ... (ไม่สนตัวพิมพ์เล็ก-ใหญ่) */
    public static Specification<Book> authorContains(String author) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    /**
     * หนังสือที่มี reading status ตามที่ระบุ.
     * Book ไม่มี mapping ไปหา Reading จึงใช้ subquery EXISTS:
     *   WHERE EXISTS (SELECT 1 FROM Reading r WHERE r.book = <book> AND r.status = :status)
     */
    public static Specification<Book> hasReadingStatus(ReadingStatus status) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Reading> reading = sub.from(Reading.class);
            sub.select(reading.get("id"));
            sub.where(
                    cb.equal(reading.get("book"), root),
                    cb.equal(reading.get("status"), status)
            );
            return cb.exists(sub);
        };
    }
}
