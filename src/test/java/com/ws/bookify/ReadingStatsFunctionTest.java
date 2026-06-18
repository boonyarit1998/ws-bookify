package com.ws.bookify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.google.gson.Gson;
import com.ws.bookify.dto.ReadingStatsResponse;
import com.ws.bookify.dto.ReadingStatsResponse.YearCount;
import com.ws.bookify.entity.Book;
import com.ws.bookify.entity.Reading;
import com.ws.bookify.entity.ReadingStatus;
import com.ws.bookify.entity.User;
import com.ws.bookify.repository.BookRepository;
import com.ws.bookify.repository.ReadingRepository;
import com.ws.bookify.repository.UserRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test ของ stored function fn_user_reading_stats (Flyway V8).
 *
 * <p>รันบน PostgreSQL จริงผ่าน Testcontainers — insert reading ของจริงแล้วเรียก function
 * ผ่าน {@link ReadingRepository#readingStats(Long)} เพื่อยืนยันว่า aggregation ที่ย้ายลง DB
 * ให้ผลตรงกับที่ ReadingStatsService คาดหวัง รวมถึงการ scope เฉพาะ user คนนั้น ๆ.
 *
 * <p>{@code @Transactional} ทำให้แต่ละเทสต์ rollback คืน DB ให้สะอาด.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class ReadingStatsFunctionTest {

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Gson gson;

    @Test
    void emptyUser_returnsZeroedStats() {
        User user = newUser("empty");

        ReadingStatsResponse stats = statsFor(user.getId());

        assertThat(stats.totalReadings()).isZero();
        // ต้องมีครบทุกสถานะ แม้ยังไม่มีข้อมูล (เป็น 0)
        assertThat(stats.byStatus())
                .containsEntry(ReadingStatus.WANT_TO_READ, 0L)
                .containsEntry(ReadingStatus.READING, 0L)
                .containsEntry(ReadingStatus.FINISHED, 0L);
        assertThat(stats.averageRating()).isEqualTo(0.0);
        assertThat(stats.totalReviews()).isZero();
        assertThat(stats.finishedPerYear()).isEmpty();
    }

    @Test
    void aggregatesAcrossStatusesRatingsReviewsAndYears() {
        User user = newUser("reader");
        // FINISHED, rating 4, มีรีวิว, อ่านจบปี 2024
        addReading(user, ReadingStatus.FINISHED, 4, "loved it", finishedIn(2024));
        // FINISHED, rating 5, รีวิวเป็นสตริงว่าง (ต้องไม่ถูกนับเป็นรีวิว), อ่านจบปี 2024
        addReading(user, ReadingStatus.FINISHED, 5, "", finishedIn(2024));
        // FINISHED, ไม่มี rating/review (rating null ต้องไม่ถูกนำไปเฉลี่ย), อ่านจบปี 2025
        addReading(user, ReadingStatus.FINISHED, null, null, finishedIn(2025));
        // READING
        addReading(user, ReadingStatus.READING, null, null, null);
        // WANT_TO_READ
        addReading(user, ReadingStatus.WANT_TO_READ, null, null, null);

        // reading ของ user อื่น — ต้องไม่หลุดเข้ามาในสถิติของ user คนแรก
        User other = newUser("other");
        addReading(other, ReadingStatus.FINISHED, 1, "noise", finishedIn(2024));

        ReadingStatsResponse stats = statsFor(user.getId());

        assertThat(stats.totalReadings()).isEqualTo(5);
        assertThat(stats.byStatus())
                .containsEntry(ReadingStatus.WANT_TO_READ, 1L)
                .containsEntry(ReadingStatus.READING, 1L)
                .containsEntry(ReadingStatus.FINISHED, 3L);
        // เฉลี่ยจาก rating ที่มีค่า: (4 + 5) / 2 = 4.5
        assertThat(stats.averageRating()).isEqualTo(4.5);
        // รีวิวที่มีข้อความจริงมีแค่ 1 (รีวิวว่างไม่ถูกนับ)
        assertThat(stats.totalReviews()).isEqualTo(1);
        // อ่านจบต่อปี เรียงตามปี: 2024 -> 2 เล่ม, 2025 -> 1 เล่ม
        assertThat(stats.finishedPerYear())
                .extracting(YearCount::year, YearCount::count)
                .containsExactly(tuple(2024, 2L), tuple(2025, 1L));
    }

    // ---- helpers ----

    /** flush ข้อมูลที่ค้างใน persistence context ลง DB ก่อน แล้วเรียก function + แปลง JSON */
    private ReadingStatsResponse statsFor(Long userId) {
        readingRepository.flush();
        return gson.fromJson(readingRepository.readingStats(userId), ReadingStatsResponse.class);
    }

    private User newUser(String name) {
        User u = new User();
        u.setUsername(name);
        u.setEmail(name + "@example.com");
        u.setPasswordHash("not-a-real-hash");
        return userRepository.saveAndFlush(u);
    }

    private void addReading(User user, ReadingStatus status, Integer rating, String review, Instant finishedAt) {
        Book book = new Book();
        book.setUserId(user.getId());
        book.setTitle("Book owned by " + user.getUsername());
        book = bookRepository.saveAndFlush(book);

        Reading r = new Reading();
        r.setUserId(user.getId());
        r.setBook(book);
        r.setStatus(status);
        r.setRating(rating);
        r.setReview(review);
        r.setFinishedAt(finishedAt);
        readingRepository.saveAndFlush(r);
    }

    /** กลางปีเพื่อกัน timezone offset ทำให้ EXTRACT(YEAR ...) เพี้ยนข้ามปี */
    private static Instant finishedIn(int year) {
        return Instant.parse(year + "-06-15T12:00:00Z");
    }
}
