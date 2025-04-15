package com.exam.repo;

import com.exam.model.QuizeResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface   QuizeResultRepository extends JpaRepository<QuizeResult,Long> {
    List<QuizeResult> findByUserId(Long userId);
    List<QuizeResult> findByQuizId(Long quizId);
    List<QuizeResult> findAllByOrderByMarksGotDescTimestampAsc();
}
