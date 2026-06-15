package com.ws.bookify.exception;

/**
 * โยนเมื่อหา resource (เช่น book/booklist) ตาม id ไม่เจอ.
 * GlobalExceptionHandler จะจับ exception นี้แล้วตอบ 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with id " + id + " not found");
    }
}
