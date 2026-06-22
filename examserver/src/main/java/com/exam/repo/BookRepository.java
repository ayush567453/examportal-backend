package com.exam.repo;

import com.exam.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    List<Book> findByGenreIgnoreCase(String genre);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableCopies > 0")
    long countAvailable();
}
