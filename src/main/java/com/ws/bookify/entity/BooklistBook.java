package com.ws.bookify.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Join entity เชื่อม Booklist กับ Book (Many-to-Many).
 * แต่ละแถว = "หนังสือเล่มนี้ อยู่ใน list นี้" พร้อมข้อมูลเพิ่ม (ลำดับ + เวลาเพิ่ม).
 *
 * ใช้ fetch = LAZY ทั้งสองฝั่ง เพื่อไม่ให้ดึง Book/Booklist มาโดยไม่จำเป็น.
 */
@Entity
@Table(
        name = "booklist_books",
        uniqueConstraints = @UniqueConstraint(columnNames = {"booklist_id", "book_id"})
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class BooklistBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booklist_id")
    private Booklist booklist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    private Integer position;

    @CreatedDate
    @Column(name = "added_at", updatable = false)
    private Instant addedAt;
}
