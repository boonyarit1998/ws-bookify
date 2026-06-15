package com.ws.bookify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request สำหรับเข้าสู่ระบบ — login ด้วย email + password.
 */
public record LoginRequest(

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        String email,

        @NotBlank(message = "password is required")
        String password
) {
}
