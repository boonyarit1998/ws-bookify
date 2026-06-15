package com.ws.bookify.repository;

import com.ws.bookify.entity.Booklist;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooklistRepository extends JpaRepository<Booklist, Long> {

    /** list ทั้งหมดของ user คนนี้ (แบ่งหน้า) */
    Page<Booklist> findByUserId(Long userId, Pageable pageable);

    /** หา list ตาม id เฉพาะที่เป็นของ user คนนี้ (scope ownership) */
    Optional<Booklist> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
