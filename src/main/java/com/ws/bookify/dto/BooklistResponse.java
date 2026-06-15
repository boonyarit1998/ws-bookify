package com.ws.bookify.dto;

import com.ws.bookify.entity.Booklist;
import java.time.Instant;

public record BooklistResponse(
        Long id,
        String name,
        String description,
        boolean isPublic,
        Instant createdAt,
        Instant updatedAt
) {
    public static BooklistResponse from(Booklist booklist) {
        return new BooklistResponse(
                booklist.getId(),
                booklist.getName(),
                booklist.getDescription(),
                booklist.isPublic(),
                booklist.getCreatedAt(),
                booklist.getUpdatedAt()
        );
    }
}
