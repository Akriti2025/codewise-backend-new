package com.codewise.codewise_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus; // NEW: Import ResponseStatus

// @ResponseStatus: Maps this exception to an HTTP status code (409 Conflict).
@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict indicates a resource conflict
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}