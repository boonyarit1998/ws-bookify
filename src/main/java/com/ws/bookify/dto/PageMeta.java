package com.ws.bookify.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/**
 * ข้อมูลการแบ่งหน้า (metadata) ที่แนบไปกับ response แบบ pagination.
 * เก็บแยกจาก data เพื่อให้ client อ่าน content กับสถานะหน้าได้ชัดเจน.
 */
public record PageMeta(
        int page,            // เลขหน้าปัจจุบัน (เริ่มที่ 0 ตามแบบ Spring Data)
        int size,            // จำนวนรายการต่อหน้า
        long totalElements,  // จำนวนรายการทั้งหมด
        int totalPages,      // จำนวนหน้าทั้งหมด
        String sortBy,       // field ที่ใช้เรียง (null ถ้าไม่ได้ระบุ sort)
        String sortOrder     // ทิศทางการเรียง "ASC"/"DESC" (null ถ้าไม่ได้ระบุ sort)
) {

    /** แปลง Spring Data Page ใดๆ เป็น PageMeta (ดึงเฉพาะ metadata ไม่เอา content) */
    public static PageMeta from(Page<?> page) {
        // Sort รองรับหลาย order ได้ แต่ meta นี้เก็บแค่ตัวแรก (order หลักที่ client ขอมา)
        Sort.Order order = page.getSort().stream().findFirst().orElse(null);
        return new PageMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                order == null ? null : order.getProperty(),
                order == null ? null : order.getDirection().name());
    }
}
