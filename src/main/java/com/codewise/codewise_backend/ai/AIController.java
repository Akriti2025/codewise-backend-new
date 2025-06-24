package com.codewise.codewise_backend.ai;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; // NEW: Import for PUT requests
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable; // NEW: Import for @PathVariable

import jakarta.persistence.EntityNotFoundException; // NEW: Import for specific exception handling

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/ai") // Base path for AI related endpoints
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Endpoint to generate an AI response for an interview question.
     * This now accepts an AIRequest object that can contain chat history for context.
     * @param request The AIRequest object with the current question and optional chat history.
     * @return ResponseEntity containing the AI's response.
     */
    @PostMapping("/generate-response")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can access this
    public ResponseEntity<AIResponse> generateAIResponse(@RequestBody AIRequest request) {
        // Basic validation: ensure the current question is not empty.
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return new ResponseEntity<>(new AIResponse("Question cannot be empty."), HttpStatus.BAD_REQUEST);
        }
        try {
            // Call AIService's getAIResponse, passing the AIRequest object with history.
            String aiAnswer = aiService.getAIResponse(request);
            return new ResponseEntity<>(new AIResponse(aiAnswer), HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception for debugging purposes.
            System.err.println("Error in AIController during AI response generation: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new AIResponse("Failed to generate AI response."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to generate practice interview questions based on a given topic and count.
     * This method directly interacts with the Gemini API to fetch questions.
     *
     * @param topic The subject area for the interview questions (e.g., "Java", "Data Structures").
     * @param count The number of questions to generate (defaults to 5 if not provided).
     * @return ResponseEntity containing a list of generated questions.
     */
    @GetMapping("/practice-questions")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can access this
    public ResponseEntity<List<String>> generatePracticeQuestions(
            @RequestParam String topic, // Topic for the questions (e.g., "Java", "Data Structures")
            @RequestParam(defaultValue = "5") int count) { // Number of questions to generate, default to 5
        try {
            // Call AIService to get practice questions.
            List<String> questions = aiService.generatePracticeQuestions(topic, count);
            if (questions.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT); // 204 No Content if no questions generated.
            }
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error in AIController while generating practice questions: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonList("Failed to generate practice questions."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to retrieve conversation history for the currently authenticated user.
     * @return ResponseEntity containing a list of ConversationHistoryResponse DTOs.
     */
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ConversationHistoryResponse>> getConversationHistory() {
        try {
            // Call AIService to retrieve the actual conversation history.
            List<ConversationHistoryResponse> history = aiService.getConversationHistory();
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error in AIController while fetching history: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * NEW ENDPOINT: Endpoint to submit feedback (rating and text) for a specific conversation.
     * @param feedbackRequest DTO containing conversation ID, rating, and feedback text.
     * @return ResponseEntity indicating success or failure.
     */
    @PutMapping("/feedback") // Using PUT as it's an update operation
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> submitFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        try {
            aiService.saveFeedback(feedbackRequest);
            return new ResponseEntity<>("Feedback submitted successfully for conversation ID: " + feedbackRequest.getConversationId(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // e.g., if conversationId is null
            System.err.println("Validation Error in AIController submitFeedback: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (EntityNotFoundException e) {
            // If the conversation ID does not exist
            System.err.println("Not Found Error in AIController submitFeedback: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (SecurityException e) {
            // If the user tries to submit feedback for a conversation they don't own
            System.err.println("Security Error in AIController submitFeedback: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN); // 403 Forbidden
        } catch (Exception e) {
            // Catch any other unexpected errors
            System.err.println("Unexpected error in AIController submitFeedback: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Failed to submit feedback due to an internal error.", HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}
