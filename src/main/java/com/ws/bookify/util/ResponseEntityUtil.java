package com.ws.bookify.util;

import com.ws.bookify.dto.ApiResponse;
import com.ws.bookify.dto.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * รวม helper สำหรับสร้าง ResponseEntity ที่ห่อด้วย ApiResponse envelope รูปแบบเดียวกัน
 * และแนบ path ของ request ให้อัตโนมัติทุกครั้ง.
 */
public class ResponseEntityUtil {

    // ---- success ----

    /** 200 OK เปล่าๆ (ไม่มี data) */
    public static ResponseEntity<Object> returnStatusOk(HttpServletRequest req) {
        return build(req, HttpStatus.OK, ApiResponse.success(null, "OK"));
    }

    /** 200 OK พร้อม data เป็น object เดี่ยว */
    public static ResponseEntity<Object> returnDataObject(HttpServletRequest req, Object data) {
        return build(req, HttpStatus.OK, ApiResponse.success(data));
    }

    /** 200 OK พร้อม data เป็น list */
    public static ResponseEntity<Object> returnDataList(HttpServletRequest req, List<?> data) {
        return build(req, HttpStatus.OK, ApiResponse.success(data));
    }

    /**
     * 200 OK พร้อม data แบบแบ่งหน้า — content + meta รวมอยู่ใน {@link PageResponse} ก้อนเดียว.
     * สร้าง PageResponse ได้ด้วย {@code PageResponse.from(page)} หรือ {@code PageResponse.of(page, mapper)}.
     */
    public static ResponseEntity<Object> returnPagination(HttpServletRequest req, PageResponse<?> page) {
        return build(req, HttpStatus.OK, ApiResponse.success(page));
    }

    // ---- error ----

    /** error แบบกำหนด status เองได้ */
    public static ResponseEntity<Object> returnStatusError(HttpServletRequest req,
                                                           HttpStatus status, String message) {
        return returnStatusError(req, status, message, null);
    }

    /** error แบบกำหนด status เองได้ + แนบรายละเอียด errors (เช่น field errors ของ validation) */
    public static ResponseEntity<Object> returnStatusError(HttpServletRequest req,
                                                           HttpStatus status, String message, Object errors) {
        return build(req, status, ApiResponse.error(message, errors));
    }

    /** 404 Not Found */
    public static ResponseEntity<Object> returnNotFound(HttpServletRequest req, String message) {
        return build(req, HttpStatus.NOT_FOUND, ApiResponse.error(message, null));
    }

    /** 500 Internal Server Error */
    public static ResponseEntity<Object> returnInternalServerError(HttpServletRequest req, String message) {
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, ApiResponse.error(message, null));
    }

    // ---- helper ----

    /** สร้าง ResponseEntity: ตั้ง status, content-type JSON, แนบ path, แล้วแปลงเป็น JSON ด้วย Gson */
    private static ResponseEntity<Object> build(HttpServletRequest req,
                                                HttpStatus status, ApiResponse<?> body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(GsonUtil.toJson(body.withPath(req.getRequestURI())));
    }
}
