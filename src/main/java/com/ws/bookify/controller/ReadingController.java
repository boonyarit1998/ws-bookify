package com.ws.bookify.controller;

import com.ws.bookify.dto.ReadingRequest;
import com.ws.bookify.service.ReadingService;
import com.ws.bookify.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller layer — สถานะการอ่าน + รีวิว ของหนังสือเล่มหนึ่ง.
 * เส้นทางซ้อนใต้ book: /api/books/{bookId}/reading
 */
@RestController
@RequestMapping("/api/books/{bookId}/reading")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;

    /** PUT /api/books/{bookId}/reading — ตั้ง/อัปเดตสถานะการอ่าน (upsert) */
    @PutMapping
    public ResponseEntity<Object> upsert(@PathVariable Long bookId,
                                         @Valid @RequestBody ReadingRequest request,
                                         HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, readingService.upsert(bookId, request));
    }

    /** GET /api/books/{bookId}/reading — ดูสถานะการอ่าน */
    @GetMapping
    public ResponseEntity<Object> get(@PathVariable Long bookId, HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, readingService.get(bookId));
    }

    /** DELETE /api/books/{bookId}/reading — ลบสถานะการอ่าน */
    @DeleteMapping
    public ResponseEntity<Object> delete(@PathVariable Long bookId, HttpServletRequest httpRequest) {
        readingService.delete(bookId);
        return ResponseEntityUtil.returnStatusOk(httpRequest);
    }
}
