package com.ws.bookify.controller;

import com.ws.bookify.dto.BookRequest;
import com.ws.bookify.dto.PageResponse;
import com.ws.bookify.entity.ReadingStatus;
import com.ws.bookify.service.BookService;
import com.ws.bookify.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller layer — REST endpoints ของ book.
 * หน้าที่: รับ HTTP request, validate input, เรียก service, แล้วคืน response
 * ผ่าน ResponseEntityUtil ให้ได้ envelope รูปแบบเดียวกันทุก path.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookRequest request,
                                         HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, bookService.create(request));
    }

    // GET /api/books?title=clean&author=martin&status=FINISHED&page=0&size=20&sort=title,asc
    // title/author/status เป็น filter optional; page/size/sort -> Pageable อัตโนมัติ
    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String title,
                                          @RequestParam(required = false) String author,
                                          @RequestParam(required = false) ReadingStatus status,
                                          Pageable pageable,
                                          HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnPagination(httpRequest,
                PageResponse.from(bookService.search(title, author, status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id, HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, bookService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody BookRequest request,
                                         HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, bookService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        bookService.delete(id);
        return ResponseEntityUtil.returnStatusOk(httpRequest);
    }
}
