package com.exam.controller;

import com.exam.service.LeaderBoardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderBoardService leaderboardService;

    @GetMapping("/alldata")
    public ResponseEntity<List<LeaderBoardService.LeaderboardEntry>> getLeaderboard() {
        List<LeaderBoardService.LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}
