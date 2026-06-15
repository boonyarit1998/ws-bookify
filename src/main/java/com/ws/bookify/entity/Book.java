package com.ws.bookify.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

/**
 * Entity layer — แทน 1 แถวในตาราง "books".
 * JPA จะ map field เหล่านี้กับคอลัมน์ในฐานข้อมูลให้อัตโนมัติ.
 */
@Entity
@Table(
        name = "books",
        // ISBN unique เฉพาะภายใน user เดียวกัน (ดู V7 migration)
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "isbn"})
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // เจ้าของหนังสือ — set ตอนสร้าง, แก้ไม่ได้ (ดู V6 migration)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    private String author;

    private String isbn;

    @Column(columnDefinition = "text")
    private String description;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
