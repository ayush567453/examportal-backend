package com.exam.service.impl;
import com.exam.model.QuizResultDto;
import com.exam.model.QuizeResult;
import com.exam.model.User;
import com.exam.model.exam.Quiz;
import com.exam.repo.QuizRepository;
import com.exam.repo.QuizeResultRepository;
import com.exam.repo.UserRepository;
import com.exam.service.QuizeResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuizResultServiceImpl implements QuizeResultService {

    @Autowired
    private QuizeResultRepository quizResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public QuizeResult saveQuizResult(QuizeResult quizResult) {
        return quizResultRepository.save(quizResult);
    }

    @Override
    public List<QuizeResult> getQuizResultsByUser(Long userId) {
        return quizResultRepository.findByUserId(userId);
    }

    @Override
    public List<QuizeResult> getQuizResultsByQuiz(Long quizId) {
        return quizResultRepository.findByQuizId(quizId);
    }

    @Override
    public List<QuizResultDto> getAllQuizResults() {
        List<QuizeResult> quizResults = quizResultRepository.findAll();
        List<QuizResultDto> quizResultDTOs = new ArrayList<>();

        for (QuizeResult quizResult : quizResults) {
            Long userId = quizResult.getUserId();
            Long quizId = quizResult.getQuizId();

            // Debugging: Print the IDs to ensure they are not null
            System.out.println("Processing QuizResult with ID: " + quizResult.getId());
            System.out.println("User ID: " + userId);
            System.out.println("Quiz ID: " + quizId);

            User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
            Quiz quiz = (quizId != null) ? quizRepository.findById(quizId).orElse(null) : null;

            // Debugging: Print whether the user and quiz were found
            System.out.println("User found: " + (user != null));
            System.out.println("Quiz found: " + (quiz != null));

            QuizResultDto dto = new QuizResultDto();
            dto.setId(quizResult.getId());
            dto.setUsername(user != null ? user.getUsername() : "Rahul");
            dto.setQuizName(quiz != null ? quiz.getTitle() : "Ramesh");
            dto.setMarksGot((int)quizResult.getMarksGot());
            dto.setCorrectAnswers(quizResult.getCorrectAnswers());
            dto.setAttempted(quizResult.getAttempted());
            dto.setTimestamp(quizResult.getTimestamp());

            quizResultDTOs.add(dto);
        }
        return quizResultDTOs;
    }
}