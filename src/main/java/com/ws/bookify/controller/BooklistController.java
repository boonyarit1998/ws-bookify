package com.ws.bookify.controller;

import com.ws.bookify.dto.BooklistRequest;
import com.ws.bookify.dto.PageResponse;
import com.ws.bookify.service.BooklistService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booklists")
@RequiredArgsConstructor
public class BooklistController {

    private final BooklistService booklistService;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BooklistRequest request,
                                         HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, booklistService.create(request));
    }

    // GET /api/booklists?page=0&size=20&sort=name,asc
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnPagination(httpRequest,
                PageResponse.from(booklistService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id, HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, booklistService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody BooklistRequest request,
                                         HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, booklistService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        booklistService.delete(id);
        return ResponseEntityUtil.returnStatusOk(httpRequest);
    }
}
