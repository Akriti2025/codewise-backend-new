package com.codewise.codewise_backend.ai; // Adjust package name if yours is different

import java.util.List;

public class AIRequest {
    private String question;
    private List<ChatMessage> chatHistory; // NEW: To hold conversation history

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    // NEW: Getters and Setters for chatHistory
    public List<ChatMessage> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<ChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
    }
}
