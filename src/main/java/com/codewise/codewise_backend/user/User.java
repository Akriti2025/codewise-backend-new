package com.codewise.codewise_backend.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column; // Make sure this import is present if not already

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections; // For empty collections

@Entity
@Table(name = "users") // Ensure table name is 'users'
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // This is typically email in real apps, but we use it as username here

    @Column(nullable = false)
    private String password;

    // NEW PROFILE FIELDS
    @Column(name = "first_name") // Good practice to map to snake_case column names
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true) // Email should often be unique
    private String email;

    @Column(name = "target_role")
    private String targetRole; // e.g., "Software Engineer", "Data Scientist"

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience; // Using Integer to allow null if not provided

    // Default constructor for JPA
    public User() {
    }

    // Constructor for registration (minimal fields)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Expanded constructor for initial profile creation or comprehensive updates
    public User(String username, String password, String firstName, String lastName, String email, String targetRole, Integer yearsOfExperience) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.targetRole = targetRole;
        this.yearsOfExperience = yearsOfExperience;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters and Setters for NEW PROFILE FIELDS
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

    // --- UserDetails Interface Implementations ---
    // For simplicity, we're returning empty collections/true for basic security setup
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // No specific roles/authorities defined yet
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", targetRole='" + targetRole + '\'' +
               ", yearsOfExperience=" + yearsOfExperience +
               '}';
    }
}
