package com.exam.repo;

import com.exam.model.BookIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByStatus(String status);
    List<BookIssue> findByMemberId(Long memberId);
    List<BookIssue> findByBookId(Long bookId);
    long countByStatus(String status);
}
