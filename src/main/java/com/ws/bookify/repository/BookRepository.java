package com.ws.bookify.repository;

import com.ws.bookify.entity.Book;
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

    boolean existsByIsbn(String isbn);
}
