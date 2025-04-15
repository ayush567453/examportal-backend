package com.exam.service;

import com.exam.model.QuizResultDto;
import com.exam.model.QuizeResult;

import java.util.List;

public interface QuizeResultService {
    QuizeResult saveQuizResult(QuizeResult quizResult);

    List<QuizeResult> getQuizResultsByUser(Long userId);

    List<QuizeResult> getQuizResultsByQuiz(Long quizId);

    List<QuizResultDto> getAllQuizResults();
}
