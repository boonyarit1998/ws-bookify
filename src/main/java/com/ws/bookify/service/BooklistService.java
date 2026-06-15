package com.ws.bookify.service;

import com.ws.bookify.dto.BooklistRequest;
import com.ws.bookify.dto.BooklistResponse;
import com.ws.bookify.entity.Booklist;
import com.ws.bookify.exception.ResourceNotFoundException;
import com.ws.bookify.repository.BooklistRepository;
import com.ws.bookify.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BooklistService {

    private final BooklistRepository booklistRepository;

    public BooklistResponse create(BooklistRequest request) {
        Booklist booklist = new Booklist();
        booklist.setUserId(SecurityUtils.currentUserId());
        applyRequest(booklist, request);
        return BooklistResponse.from(booklistRepository.save(booklist));
    }

    @Transactional(readOnly = true)
    public Page<BooklistResponse> findAll(Pageable pageable) {
        return booklistRepository.findByUserId(SecurityUtils.currentUserId(), pageable)
                .map(BooklistResponse::from);
    }

    @Transactional(readOnly = true)
    public BooklistResponse findById(Long id) {
        return BooklistResponse.from(getOrThrow(id));
    }

    public BooklistResponse update(Long id, BooklistRequest request) {
        Booklist booklist = getOrThrow(id);
        applyRequest(booklist, request);
        return BooklistResponse.from(booklistRepository.save(booklist));
    }

    public void delete(Long id) {
        if (!booklistRepository.existsByIdAndUserId(id, SecurityUtils.currentUserId())) {
            throw new ResourceNotFoundException("Booklist", id);
        }
        booklistRepository.deleteById(id);
    }

    /** ดึง list ของ user ปัจจุบัน หรือ 404 ถ้าไม่เจอ/ไม่ใช่ของเรา */
    private Booklist getOrThrow(Long id) {
        return booklistRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Booklist", id));
    }

    private void applyRequest(Booklist booklist, BooklistRequest request) {
        booklist.setName(request.name());
        booklist.setDescription(request.description());
        // ไม่ส่ง isPublic มา -> ถือเป็น false
        booklist.setPublic(Boolean.TRUE.equals(request.isPublic()));
    }
}
