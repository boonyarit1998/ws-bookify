package com.ws.bookify.dto;

/**
 * ผลลัพธ์ของ register/login — JWT พร้อมข้อมูล user.
 *
 * client ต้องแนบ token กลับมาทุก request ผ่าน header: Authorization: Bearer &lt;accessToken&gt;
 */
public record AuthResponse(
        String accessToken,
        String tokenType,    // "Bearer"
        long expiresIn,      // อายุ token (วินาที)
        UserResponse user
) {
}
