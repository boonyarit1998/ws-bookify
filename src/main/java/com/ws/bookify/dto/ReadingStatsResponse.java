package com.ws.bookify.dto;

import com.ws.bookify.entity.ReadingStatus;
import java.util.List;
import java.util.Map;

/**
 * สถิติการอ่านแบบรวม (aggregate).
 */
public record ReadingStatsResponse(
        long totalReadings,                  // จำนวน reading record ทั้งหมด
        Map<ReadingStatus, Long> byStatus,   // นับแยกตามสถานะ (มีครบทุกค่า แม้เป็น 0)
        double averageRating,                // คะแนนเฉลี่ย (0 ถ้ายังไม่มีใครให้คะแนน)
        long totalReviews,                   // จำนวนรีวิวที่มีข้อความ
        List<YearCount> finishedPerYear      // อ่านจบกี่เล่ม แยกตามปี
) {

    /** จำนวนหนังสือที่อ่านจบในปีหนึ่งๆ */
    public record YearCount(int year, long count) {
    }
}
