package org.openapitools.controllers;

import org.openapitools.model.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handle(ResponseStatusException ex) {
        ApiError err = new ApiError();
        err.setCode(ex.getStatusCode().value());
        err.setMessage(ex.getReason());
        err.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.status(ex.getStatusCode()).body(err);
    }
}