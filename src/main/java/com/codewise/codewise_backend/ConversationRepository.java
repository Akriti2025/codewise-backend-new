    package com.codewise.codewise_backend;

    import com.codewise.codewise_backend.user.User; // Import the User entity
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.List;

    public interface ConversationRepository extends JpaRepository<Conversation, Long> {
        // Find all conversations for a specific user, ordered by timestamp descending
        List<Conversation> findByUserOrderByTimestampDesc(User user);
    }
    