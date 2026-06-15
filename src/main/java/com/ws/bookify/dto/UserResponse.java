package com.ws.bookify.dto;

import com.ws.bookify.entity.User;
import java.time.Instant;

/**
 * DTO ที่ส่งกลับไปแทน User — ไม่เปิดเผย password hash.
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
