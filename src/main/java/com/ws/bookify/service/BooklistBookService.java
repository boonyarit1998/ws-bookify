package com.ws.bookify.service;

import com.ws.bookify.dto.AddBookToListRequest;
import com.ws.bookify.dto.BooklistBookResponse;
import com.ws.bookify.entity.Book;
import com.ws.bookify.entity.Booklist;
import com.ws.bookify.entity.BooklistBook;
import com.ws.bookify.exception.DuplicateResourceException;
import com.ws.bookify.exception.ResourceNotFoundException;
import com.ws.bookify.repository.BookRepository;
import com.ws.bookify.repository.BooklistBookRepository;
import com.ws.bookify.repository.BooklistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer — จัดการความสัมพันธ์ book <-> booklist (เพิ่ม/ลบ/ดูหนังสือใน list).
 *
 * Lombok @RequiredArgsConstructor สร้าง constructor ที่รับ field final ทั้งหมด
 * ให้อัตโนมัติ -> Spring ใช้ทำ constructor injection.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BooklistBookService {

    private final BooklistBookRepository booklistBookRepository;
    private final BooklistRepository booklistRepository;
    private final BookRepository bookRepository;

    /** เพิ่มหนังสือเข้า booklist */
    public BooklistBookResponse addBook(Long booklistId, AddBookToListRequest request) {
        Booklist booklist = booklistRepository.findById(booklistId)
                .orElseThrow(() -> new ResourceNotFoundException("Booklist", booklistId));
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", request.bookId()));

        if (booklistBookRepository.existsByBooklistIdAndBookId(booklistId, request.bookId())) {
            throw new DuplicateResourceException(
                    "book " + request.bookId() + " is already in booklist " + booklistId);
        }

        BooklistBook entry = new BooklistBook();
        entry.setBooklist(booklist);
        entry.setBook(book);
        // ถ้าไม่ระบุ position มา ให้ต่อท้าย list (count ปัจจุบัน)
        entry.setPosition(request.position() != null
                ? request.position()
                : (int) booklistBookRepository.countByBooklistId(booklistId));

        return BooklistBookResponse.from(booklistBookRepository.save(entry));
    }

    /** ดูหนังสือทั้งหมดใน booklist (เรียงตามลำดับ) */
    @Transactional(readOnly = true)
    public List<BooklistBookResponse> getBooks(Long booklistId) {
        if (!booklistRepository.existsById(booklistId)) {
            throw new ResourceNotFoundException("Booklist", booklistId);
        }
        return booklistBookRepository.findByBooklistIdOrderByPositionAscIdAsc(booklistId).stream()
                .map(BooklistBookResponse::from)
                .toList();
    }

    /** ลบหนังสือออกจาก booklist */
    public void removeBook(Long booklistId, Long bookId) {
        BooklistBook entry = booklistBookRepository.findByBooklistIdAndBookId(booklistId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book " + bookId + " in booklist", booklistId));
        booklistBookRepository.delete(entry);
    }
}
