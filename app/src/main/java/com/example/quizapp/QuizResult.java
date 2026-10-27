package com.example.quizapp;

import java.io.Serializable;

public class QuizResult implements Serializable {
    private String resultId;
    private String quizId;
    private String quizTitle;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private int score;
    private int total;
    private long timestamp;

    public QuizResult() {
        // Default constructor for Firebase DataSnapshot
    }

    public QuizResult(String resultId, String quizId, String quizTitle, String studentId, String studentName, String studentEmail, int score, int total, long timestamp) {
        this.resultId = resultId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.score = score;
        this.total = total;
        this.timestamp = timestamp;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
