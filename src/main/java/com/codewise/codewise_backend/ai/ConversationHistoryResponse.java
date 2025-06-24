package com.codewise.codewise_backend.ai;

import java.time.LocalDateTime;

// DTO for returning a single conversation entry in the history
public class ConversationHistoryResponse {
    private Long id;
    private String userQuestion;
    private String aiAnswer;
    private LocalDateTime timestamp;

    public ConversationHistoryResponse(Long id, String userQuestion, String aiAnswer, LocalDateTime timestamp) {
        this.id = id;
        this.userQuestion = userQuestion;
        this.aiAnswer = aiAnswer;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserQuestion() {
        return userQuestion;
    }

    public void setUserQuestion(String userQuestion) {
        this.userQuestion = userQuestion;
    }

    public String getAiAnswer() {
        return aiAnswer;
    }

    public void setAiAnswer(String aiAnswer) {
        this.aiAnswer = aiAnswer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
