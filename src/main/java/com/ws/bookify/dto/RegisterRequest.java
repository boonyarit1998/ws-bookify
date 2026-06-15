package com.ws.bookify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request สำหรับสมัครสมาชิก. เก็บทั้ง username และ email (unique ทั้งคู่).
 */
public record RegisterRequest(

        @NotBlank(message = "username is required")
        @Size(max = 50)
        String username,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        @Size(max = 255)
        String email,

        // BCrypt รองรับ input สูงสุด 72 bytes
        @NotBlank(message = "password is required")
        @Size(min = 8, max = 72, message = "password must be between 8 and 72 characters")
        String password
) {
}
