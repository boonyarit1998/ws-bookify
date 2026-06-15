package com.ws.bookify.repository;

import com.ws.bookify.entity.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository layer — data access.
 * - JpaRepository: CRUD พื้นฐาน (save/findById/findAll/deleteById ...)
 * - JpaSpecificationExecutor: query แบบ dynamic ด้วย Specification
 *   (ใช้ทำ search/filter ที่เงื่อนไข optional หลายตัว)
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    /** เช็คว่า user คนนี้มีหนังสือ ISBN นี้อยู่แล้วหรือยัง (ISBN unique ต่อ user) */
    boolean existsByUserIdAndIsbn(Long userId, String isbn);

    /** หาหนังสือตาม id เฉพาะที่เป็นของ user คนนี้ (scope ownership) */
    Optional<Book> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
