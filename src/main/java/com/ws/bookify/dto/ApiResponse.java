package com.ws.bookify.dto;

import java.time.Instant;

/**
 * Envelope มาตรฐานที่ครอบ response ของทุก endpoint ให้มีรูปแบบเดียวกัน.
 *
 * - success = true  -> มี "data"
 * - success = false -> มี "errors"
 * - path            -> URL ที่ถูกเรียก (ใส่เมื่อมี HttpServletRequest)
 *
 * หมายเหตุ: response ถูก serialize ด้วย Gson (ผ่าน ResponseEntityUtil) ซึ่งตั้ง
 * serializeNulls ไว้ จึงแสดง field ที่เป็น null ครบทุกตัว.
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Object errors,
        String path,
        Instant timestamp
) {

    /** สำเร็จ พร้อมข้อความกำหนดเอง */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, null, Instant.now());
    }

    /** สำเร็จ ใช้ข้อความ default */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "success");
    }

    /** ผิดพลาด — errors อาจเป็น Map ของ field errors หรือ null */
    public static ApiResponse<Void> error(String message, Object errors) {
        return new ApiResponse<>(false, message, null, errors, null, Instant.now());
    }

    /** คืน copy เดิมแต่เติม path (record เป็น immutable จึงสร้างใหม่) */
    public ApiResponse<T> withPath(String path) {
        return new ApiResponse<>(success, message, data, errors, path, timestamp);
    }
}
