package com.codewise.codewise_backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // NEW: This import fixes the compilation error
import org.springframework.web.bind.annotation.*; // Import all annotations from rest (GetMapping, PutMapping, RequestBody etc.)

@RestController
@RequestMapping("/api/profile") // Base path for profile endpoints
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to get the profile of the currently authenticated user
    @GetMapping // Maps to GET /api/profile
    @PreAuthorize("isAuthenticated()") // Only authenticated users can access
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // This check is a safeguard; @PreAuthorize should handle unauthenticated requests
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        try {
            User user = userService.getUserProfile(username);
            // Map the User entity to UserProfileResponse DTO
            return new ResponseEntity<>(new UserProfileResponse(user), HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            // This case should theoretically not happen if @PreAuthorize works correctly
            // and the user is found in the security context.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (Exception e) {
            System.err.println("Error fetching user profile for " + username + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // Endpoint to update the profile of the currently authenticated user
    @PutMapping // Maps to PUT /api/profile
    @PreAuthorize("isAuthenticated()") // Only authenticated users can update
    public ResponseEntity<UserProfileResponse> updateUserProfile(@RequestBody UserProfileUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        try {
            User updatedUser = userService.updateUserProfile(username, request);
            // Map the updated User entity to UserProfileResponse DTO
            return new ResponseEntity<>(new UserProfileResponse(updatedUser), HttpStatus.OK);
        } catch (UsernameNotFoundException e) { // Catch UsernameNotFoundException specifically
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (RuntimeException e) { // Catch specific runtime exceptions like email already exists check in UserService
            System.err.println("Error updating user profile for " + username + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (Exception e) {
            System.err.println("Unexpected error updating user profile for " + username + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}
