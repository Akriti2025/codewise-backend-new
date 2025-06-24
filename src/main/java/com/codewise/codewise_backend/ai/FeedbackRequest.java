package com.codewise.codewise_backend.ai;

// This DTO will be used to receive feedback data from the frontend.
public class FeedbackRequest {
    private Long conversationId; // The ID of the conversation entry being rated
    private Integer rating;      // The rating (e.g., 1-5)
    private String feedbackText; // Optional text feedback

    public FeedbackRequest() {}

    public FeedbackRequest(Long conversationId, Integer rating, String feedbackText) {
        this.conversationId = conversationId;
        this.rating = rating;
        this.feedbackText = feedbackText;
    }

    // Getters and Setters
    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }
}