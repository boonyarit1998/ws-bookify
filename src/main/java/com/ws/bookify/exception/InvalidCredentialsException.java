package com.ws.bookify.exception;

/**
 * โยนเมื่อ login ไม่ผ่าน (email ไม่มีอยู่ หรือ password ผิด).
 * GlobalExceptionHandler จะจับแล้วตอบ 401 Unauthorized.
 * ใช้ข้อความกลางๆ ("invalid email or password") เพื่อไม่บอกใบ้ว่า email มีอยู่จริงไหม.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
