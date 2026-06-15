package com.ws.bookify.controller;

import com.ws.bookify.service.ReadingStatsService;
import com.ws.bookify.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller layer — สถิติการอ่านแบบรวม.
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class ReadingStatsController {

    private final ReadingStatsService readingStatsService;

    /** GET /api/stats/reading — สรุปสถิติการอ่านทั้งหมด */
    @GetMapping("/reading")
    public ResponseEntity<Object> reading(HttpServletRequest httpRequest) {
        return ResponseEntityUtil.returnDataObject(httpRequest, readingStatsService.getStats());
    }
}
