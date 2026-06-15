package com.ws.bookify.service;

import com.ws.bookify.dto.ReadingStatsResponse;
import com.ws.bookify.dto.ReadingStatsResponse.YearCount;
import com.ws.bookify.entity.ReadingStatus;
import com.ws.bookify.repository.ReadingRepository;
import com.ws.bookify.util.SecurityUtils;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer — รวบรวมสถิติการอ่านจากหลาย aggregate query.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadingStatsService {

    private final ReadingRepository readingRepository;

    public ReadingStatsResponse getStats() {
        // สถิติเฉพาะ reading ของ user ที่ login อยู่
        Long userId = SecurityUtils.currentUserId();

        // นับแยกตาม status — เริ่มจาก 0 ทุกค่า เพื่อให้ output มีครบทุกสถานะเสมอ
        Map<ReadingStatus, Long> byStatus = new EnumMap<>(ReadingStatus.class);
        for (ReadingStatus status : ReadingStatus.values()) {
            byStatus.put(status, 0L);
        }
        for (Object[] row : readingRepository.countGroupByStatus(userId)) {
            byStatus.put((ReadingStatus) row[0], ((Number) row[1]).longValue());
        }
        long totalReadings = byStatus.values().stream().mapToLong(Long::longValue).sum();

        // คะแนนเฉลี่ย — ปัดเหลือ 2 ตำแหน่ง, 0 ถ้ายังไม่มีใครให้คะแนน
        Double avg = readingRepository.averageRating(userId);
        double averageRating = (avg == null) ? 0.0 : Math.round(avg * 100.0) / 100.0;

        long totalReviews = readingRepository.countReviews(userId);

        // อ่านจบต่อปี
        List<YearCount> finishedPerYear = readingRepository.countFinishedPerYear(userId).stream()
                .map(row -> new YearCount(((Number) row[0]).intValue(), ((Number) row[1]).longValue()))
                .toList();

        return new ReadingStatsResponse(totalReadings, byStatus, averageRating, totalReviews, finishedPerYear);
    }
}
