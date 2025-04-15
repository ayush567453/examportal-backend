package com.exam.service;

import java.util.List;

public interface LeaderBoardService {
    List<LeaderboardEntry> getLeaderboard();

    class LeaderboardEntry {
        private String username;
        private double marksGot;
        private long timestamp;

        public LeaderboardEntry(String username, double marksGot, long timestamp) {
            this.username = username;
            this.marksGot = marksGot;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public double getMarksGot() {
            return marksGot;
        }

        public void setMarksGot(double marksGot) {
            this.marksGot = marksGot;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
