package com.example.quizapp;

import java.io.Serializable;

public class Quiz implements Serializable {
    private String quizId;
    private String title;
    private String topic;
    private String teacherId;
    private String teacherName;
    private long timestamp;
    private int questionCount;

    public Quiz() {
        // Default constructor for Firebase DataSnapshot
    }

    public Quiz(String quizId, String title, String topic, String teacherId, String teacherName, long timestamp, int questionCount) {
        this.quizId = quizId;
        this.title = title;
        this.topic = topic;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.timestamp = timestamp;
        this.questionCount = questionCount;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
}
