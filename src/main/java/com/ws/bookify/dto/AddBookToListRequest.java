package com.ws.bookify.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request สำหรับเพิ่มหนังสือเข้า booklist.
 * position เป็น optional — ถ้าไม่ส่งมา service จะต่อท้าย list ให้เอง.
 */
public record AddBookToListRequest(

        @NotNull(message = "bookId is required")
        Long bookId,

        Integer position
) {
}
