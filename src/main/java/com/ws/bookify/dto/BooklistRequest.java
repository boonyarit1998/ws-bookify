package com.ws.bookify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BooklistRequest(

        @NotBlank(message = "name is required")
        @Size(max = 255)
        String name,

        String description,

        // Boolean (wrapper) ไม่ใช่ boolean — เพื่อให้ไม่ส่ง field นี้มาก็ได้ (default = false)
        Boolean isPublic
) {
}
