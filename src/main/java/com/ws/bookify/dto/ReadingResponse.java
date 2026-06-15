package com.ws.bookify.dto;

import com.ws.bookify.entity.Reading;
import com.ws.bookify.entity.ReadingStatus;
import java.time.Instant;

public record ReadingResponse(
        Long id,
        Long bookId,
        ReadingStatus status,
        Integer rating,
        String review,
        Integer currentPage,
        Instant finishedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static ReadingResponse from(Reading reading) {
        return new ReadingResponse(
                reading.getId(),
                reading.getBook().getId(),
                reading.getStatus(),
                reading.getRating(),
                reading.getReview(),
                reading.getCurrentPage(),
                reading.getFinishedAt(),
                reading.getCreatedAt(),
                reading.getUpdatedAt()
        );
    }
}
