    package com.codewise.codewise_backend.user;

    import com.codewise.codewise_backend.exceptions.UsernameAlreadyExistsException;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import java.util.Optional;

    @Service
    public class UserService implements UserDetailsService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        public User registerNewUser(String username, String password) {
            if (userRepository.findByUsername(username).isPresent()) {
                throw new UsernameAlreadyExistsException("Username '" + username + "' already exists!");
            }
            String encodedPassword = passwordEncoder.encode(password);
            User newUser = new User(username, encodedPassword);
            return userRepository.save(newUser);
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        }

        public Optional<User> findByUsername(String username) {
            return userRepository.findByUsername(username);
        }

        // NEW METHOD: getUserProfile
        // Retrieves the full User entity for profile display.
        public User getUserProfile(String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        }

        // NEW METHOD: updateUserProfile
        // Updates specific fields of the user's profile.
        public User updateUserProfile(String username, UserProfileUpdateRequest request) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            // Update fields only if they are provided in the request (not null)
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName());
            }
            // Only update email if it's different from the current one
            // and the new email is not already taken by another user's username
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                if (userRepository.findByUsername(request.getEmail()).isPresent() && !userRepository.findByUsername(request.getEmail()).get().getId().equals(user.getId())) {
                    throw new RuntimeException("Email '" + request.getEmail() + "' is already registered as another user's username.");
                }
                user.setEmail(request.getEmail());
            }
            if (request.getTargetRole() != null) {
                user.setTargetRole(request.getTargetRole());
            }
            if (request.getYearsOfExperience() != null) {
                user.setYearsOfExperience(request.getYearsOfExperience());
            }

            return userRepository.save(user); // Save the updated user entity
        }
    }
    