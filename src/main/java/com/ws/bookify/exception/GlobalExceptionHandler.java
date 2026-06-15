package com.ws.bookify.exception;

import com.ws.bookify.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * จับ exception จากทุก controller ไว้ที่เดียว แล้วส่งกลับผ่าน ResponseEntityUtil
 * เพื่อให้ error response มี envelope + path เหมือนกับ success ทุกประการ.
 * (Spring inject HttpServletRequest เข้า method ของ @ExceptionHandler ให้อัตโนมัติ)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** หา resource ไม่เจอ -> 404 */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return ResponseEntityUtil.returnNotFound(req, ex.getMessage());
    }

    /** สร้าง resource ซ้ำ (เช่น เพิ่มหนังสือซ้ำใน booklist) -> 409 Conflict */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        return ResponseEntityUtil.returnStatusError(req, HttpStatus.CONFLICT, ex.getMessage());
    }

    /** login ไม่ผ่าน (email ไม่มี/password ผิด) -> 401 Unauthorized */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest req) {
        return ResponseEntityUtil.returnStatusError(req, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * ละเมิด constraint ระดับ DB (เช่น unique ISBN ชนกันใน race condition ที่ service เช็คไม่ทัน)
     * -> 409 แทนที่จะหลุดเป็น 500. ปกติ service จะดักก่อนด้วยข้อความที่ชัดกว่า.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ResponseEntityUtil.returnStatusError(req, HttpStatus.CONFLICT,
                "resource conflicts with an existing record");
    }

    /** validation จาก @Valid ไม่ผ่าน -> 400 พร้อมรายละเอียดแต่ละ field ใน "errors" */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntityUtil.returnStatusError(req, HttpStatus.BAD_REQUEST, "validation failed", fieldErrors);
    }

    /** query param แปลง type ไม่ได้ เช่น ?status=DONE (ไม่มีใน enum) -> 400 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return ResponseEntityUtil.returnStatusError(req, HttpStatus.BAD_REQUEST,
                "invalid value for parameter '" + ex.getName() + "'");
    }

    /** body แปลงไม่ได้ เช่นส่งค่า enum ที่ไม่มีอยู่ หรือ JSON ผิดรูป -> 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return ResponseEntityUtil.returnStatusError(req, HttpStatus.BAD_REQUEST, "malformed or invalid request body");
    }

    /** อะไรที่ไม่ได้ดักไว้ -> 500 (กันไม่ให้ response หลุดรูปแบบ envelope) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception ex, HttpServletRequest req) {
        return ResponseEntityUtil.returnInternalServerError(req, "internal server error");
    }
}
