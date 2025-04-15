package com.exam.controller;

import com.exam.model.QuizResultDto;
import com.exam.model.QuizeResult;
import com.exam.service.QuizeResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/quiz-results")
public class QuizeResultController {
    @Autowired
    private QuizeResultService quizResultService;

    @PostMapping("/")
    public ResponseEntity<QuizeResult> saveQuizResult(@RequestBody QuizeResult quizResult) {
        QuizeResult savedResult = quizResultService.saveQuizResult(quizResult);
        return ResponseEntity.ok(savedResult);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizeResult>> getQuizResultsByUser(@PathVariable("userId") Long userId) {
        List<QuizeResult> results = quizResultService.getQuizResultsByUser(userId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizeResult>> getQuizResultsByQuiz(@PathVariable("quizId") Long quizId) {
        List<QuizeResult> results = quizResultService.getQuizResultsByQuiz(quizId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<QuizResultDto>> getAllQuizResults() {
        List<QuizResultDto> results = quizResultService.getAllQuizResults();
        return ResponseEntity.ok(results);
    }
}
