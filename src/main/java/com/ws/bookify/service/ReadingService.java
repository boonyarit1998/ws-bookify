package com.ws.bookify.service;

import com.ws.bookify.dto.ReadingRequest;
import com.ws.bookify.dto.ReadingResponse;
import com.ws.bookify.entity.Book;
import com.ws.bookify.entity.Reading;
import com.ws.bookify.entity.ReadingStatus;
import com.ws.bookify.exception.ResourceNotFoundException;
import com.ws.bookify.repository.BookRepository;
import com.ws.bookify.repository.ReadingRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer — สถานะการอ่าน + รีวิว ของหนังสือ.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReadingService {

    private final ReadingRepository readingRepository;
    private final BookRepository bookRepository;

    /**
     * ตั้งหรืออัปเดตสถานะการอ่านของหนังสือ (upsert).
     * ถ้ายังไม่มี reading ของหนังสือเล่มนี้ -> สร้างใหม่, ถ้ามีแล้ว -> อัปเดต.
     */
    public ReadingResponse upsert(Long bookId, ReadingRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));

        Reading reading = readingRepository.findByBookId(bookId)
                .orElseGet(() -> {
                    Reading r = new Reading();
                    r.setBook(book);
                    return r;
                });

        reading.setStatus(request.status());
        reading.setRating(request.rating());
        reading.setReview(request.review());
        reading.setCurrentPage(request.currentPage());

        // บันทึกเวลาที่อ่านจบ: set ครั้งแรกที่เป็น FINISHED, ล้างถ้าเปลี่ยนกลับ
        if (request.status() == ReadingStatus.FINISHED) {
            if (reading.getFinishedAt() == null) {
                reading.setFinishedAt(Instant.now());
            }
        } else {
            reading.setFinishedAt(null);
        }

        return ReadingResponse.from(readingRepository.save(reading));
    }

    /** ดูสถานะการอ่านของหนังสือ */
    @Transactional(readOnly = true)
    public ReadingResponse get(Long bookId) {
        Reading reading = readingRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading for book", bookId));
        return ReadingResponse.from(reading);
    }

    /** ลบสถานะการอ่านของหนังสือ */
    public void delete(Long bookId) {
        Reading reading = readingRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading for book", bookId));
        readingRepository.delete(reading);
    }
}
