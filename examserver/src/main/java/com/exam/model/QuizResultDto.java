package com.exam.model;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class QuizResultDto {
    private Long id;
    private String username;
    private String quizName;
    private int marksGot;
    private int correctAnswers;
    private int attempted;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public int getAttempted() {
        return attempted;
    }

    public void setAttempted(int attempted) {
        this.attempted = attempted;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getMarksGot() {
        return marksGot;
    }

    public void setMarksGot(int marksGot) {
        this.marksGot = marksGot;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
