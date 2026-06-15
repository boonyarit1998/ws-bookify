package com.ws.bookify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO ที่ client ส่งเข้ามาตอน "สร้าง" หรือ "แก้ไข" book.
 * ใช้ record เพราะเป็น immutable data carrier — สั้นและอ่านง่าย.
 * Annotation @NotBlank/@Size คือ validation ที่จะถูกตรวจตอนเข้า controller.
 */
public record BookRequest(

        @NotBlank(message = "title is required")
        @Size(max = 255)
        String title,

        @Size(max = 255)
        String author,

        @Size(max = 20)
        String isbn,

        String description
) {
}
