package com.example.quizapp;

import java.io.Serializable;

public class Question implements Serializable {
    private String questionId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctOption; // 1: A, 2: B, 3: C, 4: D

    public Question() {
        // Default constructor for Firebase DataSnapshot
    }

    public Question(String questionId, String questionText, String optionA, String optionB, String optionC, String optionD, int correctOption) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    public String getCorrectAnswerText() {
        switch (correctOption) {
            case 1: return "Option A: " + optionA;
            case 2: return "Option B: " + optionB;
            case 3: return "Option C: " + optionC;
            case 4: return "Option D: " + optionD;
            default: return "Not specified";
        }
    }
}
