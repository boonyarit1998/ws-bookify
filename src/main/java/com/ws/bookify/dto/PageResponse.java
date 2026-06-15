package com.ws.bookify.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Response แบบแบ่งหน้าที่รวม content + metadata ไว้ใน object เดียว (typed).
 *
 * ผูก content เข้ากับ PageMeta เป็นก้อนเดียว แล้วส่งเป็น data ของ {@link ApiResponse}
 * เพื่อให้ client อ่าน type ได้ชัดเจน (List&lt;T&gt; + metadata อยู่ด้วยกัน).
 *
 * โครงสร้างที่ส่งออก:
 * <pre>
 * {
 *   "content": [ ... ],
 *   "page": { "page": 0, "size": 20, "totalElements": 57, ... }
 * }
 * </pre>
 */
public record PageResponse<T>(
        List<T> content,   // รายการในหน้าปัจจุบัน
        PageMeta page      // metadata การแบ่งหน้า (reuse ตัวเดิม)
) {

    /** แปลง Spring Data Page เป็น PageResponse โดยตรง (content + meta จาก page เดียวกัน) */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), PageMeta.from(page));
    }

    /**
     * กรณี map entity -> DTO: ดึง content จาก Page ต้นทาง แปลงด้วย mapper
     * แต่ยังใช้ metadata จาก Page เดิม (จำนวนหน้า/รายการทั้งหมดไม่เปลี่ยน).
     *
     * ตัวอย่าง: {@code PageResponse.of(bookPage, BookResponse::from)}
     */
    public static <S, T> PageResponse<T> of(Page<S> page, java.util.function.Function<S, T> mapper) {
        List<T> mapped = page.getContent().stream().map(mapper).toList();
        return new PageResponse<>(mapped, PageMeta.from(page));
    }
}
