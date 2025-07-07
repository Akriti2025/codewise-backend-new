package com.codewise.codewise_backend.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.codewise.codewise_backend.Conversation;
import com.codewise.codewise_backend.ConversationRepository;
import com.codewise.codewise_backend.user.User;
import com.codewise.codewise_backend.user.UserService;

import java.time.LocalDateTime; // Required for Feedback entity's timestamp
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final ConversationRepository conversationRepository;
    private final UserService userService;
    private final FeedbackRepository feedbackRepository; // NEW: Injected FeedbackRepository

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    // Modified constructor to include FeedbackRepository
    public AIService(RestTemplate restTemplate,
                     ConversationRepository conversationRepository,
                     UserService userService,
                     FeedbackRepository feedbackRepository) { // NEW: Add to constructor
        this.restTemplate = restTemplate;
        this.conversationRepository = conversationRepository;
        this.userService = userService;
        this.feedbackRepository = feedbackRepository; // NEW: Assign
    }

    // Method to get an AI response for a question, now with chat history context and conversation saving
    public String getAIResponse(AIRequest aiRequest) {
        // Get the currently authenticated user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure authentication exists and principal is UserDetails
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            System.err.println("AIService: No authenticated UserDetails found for conversation saving.");
            return "Error: Authentication required to generate AI response and save conversation.";
        }

        // Get the username directly from UserDetails. This makes it effectively final.
        final String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Fetch the User entity from the database using the username
        User currentUser = userService.findByUsername(currentUsername)
                                     .orElseThrow(() -> new RuntimeException("User not found in database: " + currentUsername));

        List<Map<String, Object>> contents = new ArrayList<>();

        // Add previous chat history to the contents payload
        if (aiRequest.getChatHistory() != null) {
            for (ChatMessage message : aiRequest.getChatHistory()) {
                Map<String, Object> part = new HashMap<>();
                part.put("text", message.getContent());

                Map<String, Object> content = new HashMap<>();
                content.put("role", message.getRole()); // Role will be "user" or "model"
                content.put("parts", Collections.singletonList(part));
                contents.add(content);
            }
        }

        // Add the current user question to the contents payload
        Map<String, Object> currentQuestionPart = new HashMap<>();
        currentQuestionPart.put("text", aiRequest.getQuestion());

        Map<String, Object> currentQuestionContent = new HashMap<>();
        currentQuestionContent.put("role", "user");
        currentQuestionContent.put("parts", Collections.singletonList(currentQuestionPart));
        contents.add(currentQuestionContent);


        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", contents); // Use the list with history

        // Set up headers for the API request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity (payload + headers)
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        // Construct the full API URL with the API key
        String fullApiUrl = geminiApiUrl + "?key=" + geminiApiKey;

        String aiAnswer = "Failed to generate AI response."; // Default error message if something goes wrong

        try {
            // Make the POST request to Gemini API
            ResponseEntity<Map> response = restTemplate.postForEntity(fullApiUrl, requestEntity, Map.class);

            // Process the response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("candidates")) {
                    Object candidates = responseBody.get("candidates");
                    if (candidates instanceof java.util.List) {
                        java.util.List<?> candidatesList = (java.util.List<?>) candidates;
                        if (!candidatesList.isEmpty()) {
                            Object firstCandidate = candidatesList.get(0);
                            if (firstCandidate instanceof Map) {
                                Map<?, ?> candidateMap = (Map<?, ?>) firstCandidate;
                                if (candidateMap.containsKey("content")) {
                                    Object contentObj = candidateMap.get("content");
                                    if (contentObj instanceof Map) {
                                        Map<?, ?> contentMap = (Map<?, ?>) contentObj;
                                        if (contentMap.containsKey("parts")) {
                                            Object parts = contentMap.get("parts");
                                            if (parts instanceof java.util.List) {
                                                java.util.List<?> partsList = (java.util.List<?>) parts;
                                                if (!partsList.isEmpty()) {
                                                    Object firstPart = partsList.get(0);
                                                    if (firstPart instanceof Map) {
                                                        Map<?, ?> firstPartMap = (Map<?, ?>) firstPart;
                                                        if (firstPartMap.containsKey("text")) {
                                                            aiAnswer = firstPartMap.get("text").toString();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                aiAnswer = "AI Service returned non-2xx status: " + response.getStatusCode();
            }
        } catch (Exception apiException) {
            // Log the exception for debugging purposes
            System.err.println("Error communicating with Gemini API: " + apiException.getMessage());
            apiException.printStackTrace();
            aiAnswer = "Error communicating with AI service: " + apiException.getMessage();
        }

        // NEW LOGIC: Save the conversation to the database
        try {
            Conversation conversation = new Conversation(currentUser, aiRequest.getQuestion(), aiAnswer);
            conversationRepository.save(conversation);
            System.out.println("Saved conversation for user '" + currentUser.getUsername() + "'. Question: '" + aiRequest.getQuestion().substring(0, Math.min(aiRequest.getQuestion().length(), 70)) + "...'");
        } catch (Exception saveException) {
            System.err.println("Error saving conversation to database for user '" + currentUser.getUsername() + "': " + saveException.getMessage());
            saveException.printStackTrace();
            // Do not fail the AI response if saving to DB fails, but log the error
        }

        return aiAnswer; // Return the AI's generated answer
    }

    // NEW METHOD: Generates a list of practice interview questions
    public List<String> generatePracticeQuestions(String topic, int count) {
        // Construct a clear prompt for Gemini to generate questions
        String prompt = String.format("Generate %d interview questions about %s. Provide each question on a new line and start each question with a number followed by a period (e.g., '1. Question one').", count, topic);

        // Reusing the core AI calling logic (similar to getAIResponse but with a specific prompt)
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> userPart = new HashMap<>();
        userPart.put("text", prompt);

        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");
        userContent.put("parts", Collections.singletonList(userPart));
        contents.add(userContent);

        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        String fullApiUrl = geminiApiUrl + "?key=" + geminiApiKey;

        List<String> questions = Collections.emptyList(); // Initialize with empty list

        try { // Outer try block for the API call
            ResponseEntity<Map> response = restTemplate.postForEntity(fullApiUrl, requestEntity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("candidates")) {
                    Object candidates = responseBody.get("candidates");
                    if (candidates instanceof List) {
                        List<?> candidatesList = (List<?>) candidates;
                        if (!candidatesList.isEmpty()) {
                            Object firstCandidate = candidatesList.get(0);
                            if (firstCandidate instanceof Map) {
                                Map<?, ?> candidateMap = (Map<?, ?>) firstCandidate;
                                if (candidateMap.containsKey("content")) {
                                    Object contentObj = candidateMap.get("content");
                                    if (contentObj instanceof Map) {
                                        Map<?, ?> contentMap = (Map<?, ?>) contentObj;
                                        if (contentMap.containsKey("parts")) {
                                            Object parts = contentMap.get("parts");
                                            if (parts instanceof List) {
                                                List<?> partsList = (List<?>) parts;
                                                if (!partsList.isEmpty()) {
                                                    Object firstPart = partsList.get(0);
                                                    if (firstPart instanceof Map) {
                                                        Map<?, ?> firstPartMap = (Map<?, ?>) firstPart;
                                                        if (firstPartMap.containsKey("text")) {
                                                            String rawResponse = firstPartMap.get("text").toString();
                                                            // Parse the raw response into a list of questions
                                                            // Splitting by newline and filtering empty lines
                                                            questions = Arrays.stream(rawResponse.split("\n"))
                                                                            .map(String::trim)
                                                                            .filter(line -> !line.isEmpty() && (line.matches("^\\d+\\..*") || line.matches("^-.*"))) // Filter numbered or dashed lists
                                                                            .collect(Collectors.toList());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.err.println("Failed to get a valid AI response for practice questions: " + response.getStatusCode());
                questions = Collections.singletonList("Failed to generate practice questions. Status: " + response.getStatusCode());
            }
        } catch (Exception e) { // Correctly placed catch block for the outer try
            System.err.println("Error calling Gemini API for practice questions: " + e.getMessage());
            e.printStackTrace();
            questions = Collections.singletonList("Error generating practice questions: " + e.getMessage());
        }
        return questions; // Return the generated questions or error message
    }

    // Method to get conversation history for the authenticated user
    public List<ConversationHistoryResponse> getConversationHistory() {
        // Get the currently authenticated user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // This is the correct way to declare and initialize currentUsername to ensure it's effectively final.
        // It gets its value once and is not reassigned within the method.
        final String currentUsername;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            System.err.println("AIService: No authenticated UserDetails found for fetching conversation history.");
            return Collections.emptyList(); // Return empty list if no user
        }

        // Fetch the User entity from the database using the username
        User currentUser = userService.findByUsername(currentUsername)
                                     .orElseThrow(() -> new RuntimeException("User not found in database: " + currentUsername));

        // Retrieve conversations from the repository for the current user, ordered by timestamp descending
        List<Conversation> conversations = conversationRepository.findByUserOrderByTimestampDesc(currentUser);

        // Map Conversation entities to ConversationHistoryResponse DTOs for client consumption
        return conversations.stream()
                            .map(conv -> new ConversationHistoryResponse(
                                    conv.getId(),
                                    conv.getUserQuestion(),
                                    conv.getAiAnswer(),
                                    conv.getTimestamp()
                            ))
                            .collect(Collectors.toList());
    }

    /**
     * NEW METHOD: Saves feedback provided by the user.
     * This method is added to resolve the "cannot find symbol" error for saveFeedback.
     *
     * @param feedbackRequest The request object containing feedback details.
     */
    public void saveFeedback(FeedbackRequest feedbackRequest) {
        // Get the currently authenticated user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            System.err.println("AIService: No authenticated UserDetails found for saving feedback. Cannot save feedback without a user.");
            throw new RuntimeException("Authentication required to save feedback.");
        }

        final String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        User currentUser = userService.findByUsername(currentUsername)
                                     .orElseThrow(() -> new RuntimeException("User not found in database for feedback: " + currentUsername));

        try {
            // Create a new Feedback entity from the FeedbackRequest
            // IMPORTANT: This assumes FeedbackRequest has getFeedbackText() and getRating() methods.
            // Adjust if your FeedbackRequest DTO has different field names.
            Feedback feedback = new Feedback(currentUser, feedbackRequest.getFeedbackText(), feedbackRequest.getRating());

            // If you want to link feedback to a specific conversation, and Feedback entity has a 'conversation' field:
            // if (feedbackRequest.getConversationId() != null) {
            //     conversationRepository.findById(feedbackRequest.getConversationId()).ifPresent(feedback::setConversation);
            // }

            feedbackRepository.save(feedback);
            System.out.println("Feedback saved successfully for user: " + currentUser.getUsername());
        } catch (Exception e) {
            System.err.println("Error saving feedback for user '" + currentUser.getUsername() + "': " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save feedback.", e);
        }
    }
}