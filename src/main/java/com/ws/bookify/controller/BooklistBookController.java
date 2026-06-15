package com.ws.bookify.controller;

import com.ws.bookify.dto.AddBookToListRequest;
import com.ws.bookify.service.BooklistBookService;
import com.ws.bookify.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller layer — จัดการหนังสือภายใน booklist หนึ่งๆ.
 * เส้นทางซ้อนใต้ booklist สื่อความสัมพันธ์ชัดเจน: /api/booklists/{id}/books
 */
@RestController
@RequestMapping("/api/booklists/{booklistId}/books")
@RequiredArgsConstructor
public class BooklistBookController {

    private final BooklistBookService booklistBookService;

    /** GET /api/booklists/{id}/books — ดูหนังสือทั้งหมดใน list */
    @GetMapping
    public ResponseEntity<Object> findAll(@PathVariable Long booklistId, HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataList(httpRequest, booklistBookService.getBooks(booklistId));
    }

    /** POST /api/booklists/{id}/books — เพิ่มหนังสือเข้า list */
    @PostMapping
    public ResponseEntity<Object> add(@PathVariable Long booklistId,
                                      @Valid @RequestBody AddBookToListRequest request,
                                      HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest,
                booklistBookService.addBook(booklistId, request));
    }

    /** DELETE /api/booklists/{id}/books/{bookId} — เอาหนังสือออกจาก list */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> remove(@PathVariable Long booklistId,
                                         @PathVariable Long bookId,
                                         HttpServletRequest httpRequest) {
        booklistBookService.removeBook(booklistId, bookId);
        return ResponseEntityUtil.returnStatusOk(httpRequest);
    }
}
