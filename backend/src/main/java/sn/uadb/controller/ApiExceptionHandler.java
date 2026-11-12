package sn.uadb.controller;

import java.time.OffsetDateTime;

import org.openapitools.model.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handle(ResponseStatusException ex) {
        return build(ex.getStatusCode().value(), ex.getReason());
    }

    // Validation du contrat (@Pattern sur le matricule). Deux exceptions possibles selon
    // que la validation passe par un proxy AOP ou par Spring MVC.
    @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ApiError> handleValidation(Exception ex) {
        // Message fixe : on ne renvoie pas le texte de l'exception (détails internes)
        return build(HttpStatus.BAD_REQUEST.value(),
                "Format de matricule invalide (doit comporter exactement 7 chiffres).");
    }

    private static ResponseEntity<ApiError> build(int status, String message) {
        ApiError err = new ApiError();
        err.setError(status);
        err.setMessage(message);
        err.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.status(status).body(err);
    }
}