package com.codewise.codewise_backend;

import com.codewise.codewise_backend.user.User; // Import the User entity
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations") // Table to store AI Q&A history
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many conversations can belong to one user
    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetch to avoid loading user eagerly
    @JoinColumn(name = "user_id", nullable = false) // THIS LINE MUST BE @JoinColumn
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userQuestion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String aiAnswer;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // NEW FIELDS FOR FEEDBACK
    @Column(name = "rating") // Rating for the AI's answer (e.g., 1-5 stars)
    private Integer rating; // Using Integer to allow null if no rating is provided

    @Column(name = "feedback_text", columnDefinition = "TEXT") // Optional detailed text feedback
    private String feedbackText;

    // Default constructor
    public Conversation() {
        this.timestamp = LocalDateTime.now(); // Set timestamp on creation
    }

    // Constructor for creating new conversation entries (without feedback initially)
    public Conversation(User user, String userQuestion, String aiAnswer) {
        this.user = user;
        this.userQuestion = userQuestion;
        this.aiAnswer = aiAnswer;
        this.timestamp = LocalDateTime.now(); // Set timestamp on creation
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    // NEW Getters and Setters for Feedback
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

    @Override
    public String toString() {
        return "Conversation{" +
               "id=" + id +
               ", userId=" + (user != null ? user.getId() : "null") +
               ", userQuestion='" + userQuestion + '\'' +
               ", aiAnswer='" + aiAnswer + '\'' +
               ", timestamp=" + timestamp +
               ", rating=" + rating +
               ", feedbackText='" + feedbackText + '\'' +
               '}';
    }
}
