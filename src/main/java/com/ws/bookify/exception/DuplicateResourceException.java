package com.ws.bookify.exception;

/**
 * โยนเมื่อพยายามสร้าง resource ที่มีอยู่แล้ว (เช่น เพิ่มหนังสือซ้ำใน booklist เดียวกัน).
 * GlobalExceptionHandler จะจับแล้วตอบ 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
