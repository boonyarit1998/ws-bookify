package com.ws.bookify.dto;

import com.ws.bookify.entity.ReadingStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Request สำหรับตั้ง/อัปเดตสถานะการอ่าน + รีวิว ของหนังสือ.
 * rating, review, currentPage เป็น optional (ใส่หรือไม่ก็ได้).
 */
public record ReadingRequest(

        @NotNull(message = "status is required")
        ReadingStatus status,

        @Min(value = 1, message = "rating must be between 1 and 5")
        @Max(value = 5, message = "rating must be between 1 and 5")
        Integer rating,

        String review,

        @PositiveOrZero(message = "currentPage must be >= 0")
        Integer currentPage
) {
}
