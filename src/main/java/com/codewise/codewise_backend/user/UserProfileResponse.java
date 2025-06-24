    package com.codewise.codewise_backend.user;

    public class UserProfileResponse {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String targetRole;
        private Integer yearsOfExperience;

        // Constructor to map from User entity
        public UserProfileResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.targetRole = user.getTargetRole();
            this.yearsOfExperience = user.getYearsOfExperience();
        }

        // Getters only (as this is a response DTO)
        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getTargetRole() {
            return targetRole;
        }

        public Integer getYearsOfExperience() {
            return yearsOfExperience;
        }
    }
    