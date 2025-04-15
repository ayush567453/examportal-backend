package com.exam.service.impl;

import com.exam.model.QuizeResult;
import com.exam.model.User;
import com.exam.repo.QuizeResultRepository;
import com.exam.repo.UserRepository;
import com.exam.service.LeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardServiceImpl implements LeaderBoardService {

    @Autowired
    private QuizeResultRepository quizResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<LeaderboardEntry> getLeaderboard() {
        List<QuizeResult> results = quizResultRepository.findAllByOrderByMarksGotDescTimestampAsc();
        return results.stream()
                .map(result -> {
                    Long userId = result.getUserId();
                    String username = "Unknown";

                    if (userId != null) {
                        User user = userRepository.findById(userId).orElse(null);
                        if (user != null) {
                            username = user.getUsername();
                        }
                    }

                    return new LeaderboardEntry(username, result.getMarksGot(), result.getTimestamp().getTime());
                })
                .collect(Collectors.toList());
    }
}
