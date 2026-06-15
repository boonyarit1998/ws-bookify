package com.ws.bookify.service;

import com.ws.bookify.dto.BookRequest;
import com.ws.bookify.dto.BookResponse;
import com.ws.bookify.entity.Book;
import com.ws.bookify.entity.ReadingStatus;
import com.ws.bookify.exception.DuplicateResourceException;
import com.ws.bookify.exception.ResourceNotFoundException;
import com.ws.bookify.repository.BookRepository;
import com.ws.bookify.repository.spec.BookSpecifications;
import com.ws.bookify.util.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service layer — business logic ของ book.
 * รับ/คืนค่าเป็น DTO, คุยกับฐานข้อมูลผ่าน repository, และจัดการ transaction.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // CREATE
    public BookResponse create(BookRequest request) {
        Long userId = SecurityUtils.currentUserId();
        requireIsbnAvailable(userId, request.isbn());
        Book book = new Book();
        book.setUserId(userId);
        applyRequest(book, request);
        Book saved = bookRepository.save(book);
        return BookResponse.from(saved);
    }

    // READ (search + filter + แบ่งหน้า)
    // filter ตัวไหนเป็น null/ว่าง จะถูกข้าม -> ไม่ใส่ filter เลย = คืนทุกเล่ม
    @Transactional(readOnly = true)
    public Page<BookResponse> search(String title, String author, ReadingStatus status, Pageable pageable) {
        List<Specification<Book>> specs = new ArrayList<>();
        // scope: เห็นเฉพาะหนังสือของตัวเอง
        specs.add(BookSpecifications.hasUserId(SecurityUtils.currentUserId()));
        if (StringUtils.hasText(title)) {
            specs.add(BookSpecifications.titleContains(title));
        }
        if (StringUtils.hasText(author)) {
            specs.add(BookSpecifications.authorContains(author));
        }
        if (status != null) {
            specs.add(BookSpecifications.hasReadingStatus(status));
        }

        // allOf รวมทุกเงื่อนไขด้วย AND; ถ้า list ว่าง = ไม่มีเงื่อนไข (คืนทั้งหมด)
        Specification<Book> spec = Specification.allOf(specs);
        return bookRepository.findAll(spec, pageable).map(BookResponse::from);
    }

    // READ (one)
    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return BookResponse.from(getOrThrow(id));
    }

    // UPDATE
    public BookResponse update(Long id, BookRequest request) {
        Book book = getOrThrow(id);
        // เช็คเฉพาะตอน ISBN เปลี่ยนไปชนเล่มอื่นของ user คนเดียวกัน
        if (StringUtils.hasText(request.isbn()) && !request.isbn().equals(book.getIsbn())) {
            requireIsbnAvailable(book.getUserId(), request.isbn());
        }
        applyRequest(book, request);
        return BookResponse.from(bookRepository.save(book));
    }

    // DELETE
    public void delete(Long id) {
        if (!bookRepository.existsByIdAndUserId(id, SecurityUtils.currentUserId())) {
            throw new ResourceNotFoundException("Book", id);
        }
        bookRepository.deleteById(id);
    }

    /**
     * ดึง entity ของ user ปัจจุบัน หรือโยน 404 ถ้าไม่เจอ/ไม่ใช่ของเรา.
     * (ตอบ 404 แทน 403 เพื่อไม่บอกใบ้ว่าหนังสือ id นี้มีอยู่จริงของคนอื่น)
     */
    private Book getOrThrow(Long id) {
        return bookRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }

    /** กัน ISBN ซ้ำในคลังของ user คนเดียวกัน -> 409 (ข้าม ถ้า isbn ว่าง/null) */
    private void requireIsbnAvailable(Long userId, String isbn) {
        if (StringUtils.hasText(isbn) && bookRepository.existsByUserIdAndIsbn(userId, isbn)) {
            throw new DuplicateResourceException("you already have a book with ISBN " + isbn);
        }
    }

    /** คัดลอกค่าจาก request -> entity (ใช้ทั้งตอน create และ update) */
    private void applyRequest(Book book, BookRequest request) {
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setDescription(request.description());
    }
}
