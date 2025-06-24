            package com.codewise.codewise_backend.user;

            public class UserProfileUpdateRequest {
                private String firstName;
                private String lastName;
                private String email;
                private String targetRole;
                private Integer yearsOfExperience;

                // Getters and Setters
                public String getFirstName() {
                    return firstName;
                }

                public void setFirstName(String firstName) {
                    this.firstName = firstName;
                }

                public String getLastName() {
                    return lastName;
                }

                public void setLastName(String lastName) {
                    this.lastName = lastName;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public String getTargetRole() {
                    return targetRole;
                }

                public void setTargetRole(String targetRole) {
                    this.targetRole = targetRole;
                }

                public Integer getYearsOfExperience() {
                    return yearsOfExperience;
                }

                public void setYearsOfExperience(Integer yearsOfExperience) {
                    this.yearsOfExperience = yearsOfExperience;
                }
            }
            