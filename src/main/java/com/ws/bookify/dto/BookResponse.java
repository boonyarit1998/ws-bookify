package com.ws.bookify.dto;

import com.ws.bookify.entity.Book;
import java.time.Instant;

/**
 * DTO ที่เราส่งกลับไปให้ client. แยกจาก entity เพื่อควบคุมว่าจะเปิดเผย field ไหน.
 */
public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    /** helper แปลงจาก entity -> response */
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
