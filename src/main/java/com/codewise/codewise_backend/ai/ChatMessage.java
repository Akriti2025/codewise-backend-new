package com.codewise.codewise_backend.ai;

/**
 * Data Transfer Object (DTO) representing a single message in a chat conversation.
 * Used to send conversation history to the AI model for contextual responses.
 */
public class ChatMessage {
    // The role of the message sender, typically "user" or "model" (for AI responses).
    private String role;
    // The content (text) of the message.
    private String content;

    // Default constructor for deserialization.
    public ChatMessage() {}

    /**
     * Constructor to create a new ChatMessage.
     * @param role The role of the sender ("user" or "model").
     * @param content The text content of the message.
     */
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Getters and Setters for the properties.

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
