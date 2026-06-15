package com.ws.bookify.repository;

import com.ws.bookify.entity.Booklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooklistRepository extends JpaRepository<Booklist, Long> {
}
