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
import com.ws.bookify.util.SecurityUtils;
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

    /** เพิ่มหนังสือเข้า booklist (ทั้ง list และหนังสือต้องเป็นของ user คนนี้) */
    public BooklistBookResponse addBook(Long booklistId, AddBookToListRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Booklist booklist = booklistRepository.findByIdAndUserId(booklistId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booklist", booklistId));
        Book book = bookRepository.findByIdAndUserId(request.bookId(), userId)
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

    /** ดูหนังสือทั้งหมดใน booklist (เรียงตามลำดับ) — เฉพาะ list ของ user คนนี้ */
    @Transactional(readOnly = true)
    public List<BooklistBookResponse> getBooks(Long booklistId) {
        requireOwnedBooklist(booklistId);
        return booklistBookRepository.findByBooklistIdOrderByPositionAscIdAsc(booklistId).stream()
                .map(BooklistBookResponse::from)
                .toList();
    }

    /** ลบหนังสือออกจาก booklist — เฉพาะ list ของ user คนนี้ */
    public void removeBook(Long booklistId, Long bookId) {
        requireOwnedBooklist(booklistId);
        BooklistBook entry = booklistBookRepository.findByBooklistIdAndBookId(booklistId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book " + bookId + " in booklist", booklistId));
        booklistBookRepository.delete(entry);
    }

    /** ตรวจว่า booklist นี้เป็นของ user ปัจจุบัน ไม่งั้น 404 */
    private void requireOwnedBooklist(Long booklistId) {
        if (!booklistRepository.existsByIdAndUserId(booklistId, SecurityUtils.currentUserId())) {
            throw new ResourceNotFoundException("Booklist", booklistId);
        }
    }
}
