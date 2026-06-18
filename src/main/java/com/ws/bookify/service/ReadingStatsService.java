package com.ws.bookify.service;

import com.google.gson.Gson;
import com.ws.bookify.dto.ReadingStatsResponse;
import com.ws.bookify.repository.ReadingRepository;
import com.ws.bookify.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer — รวบรวมสถิติการอ่านของ user ที่ login อยู่.
 *
 * <p>งานคำนวณทั้งหมด (นับตาม status, คะแนนเฉลี่ย, จำนวนรีวิว, อ่านจบต่อปี) ถูกย้ายไปอยู่ใน
 * stored function {@code fn_user_reading_stats} (Flyway V8) ซึ่งคืนผลเป็น JSON ก้อนเดียว
 * จบใน round-trip เดียว. ที่นี่เพียงให้ Gson แปลง JSON นั้นกลับเป็น {@link ReadingStatsResponse}.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadingStatsService {

    private final ReadingRepository readingRepository;
    private final Gson gson;

    public ReadingStatsResponse getStats() {
        Long userId = SecurityUtils.currentUserId();
        String statsJson = readingRepository.readingStats(userId);
        return gson.fromJson(statsJson, ReadingStatsResponse.class);
    }
}
