package com.ws.bookify.dto;

import com.ws.bookify.entity.BooklistBook;
import java.time.Instant;

/**
 * Response แสดงหนังสือ 1 เล่มภายใน booklist พร้อมข้อมูลความสัมพันธ์ (ลำดับ + เวลาเพิ่ม).
 */
public record BooklistBookResponse(
        Long id,
        BookResponse book,
        Integer position,
        Instant addedAt
) {
    public static BooklistBookResponse from(BooklistBook bb) {
        return new BooklistBookResponse(
                bb.getId(),
                BookResponse.from(bb.getBook()),
                bb.getPosition(),
                bb.getAddedAt()
        );
    }
}
