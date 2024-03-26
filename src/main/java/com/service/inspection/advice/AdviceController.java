package com.service.inspection.advice;

import jakarta.persistence.PersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Error> resolvePersistenceException(PersistenceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<ValidationErrorResponse.Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorResponse.Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrorResponse(violations));
    }

    @ExceptionHandler(S3Exception.class) // TODO(Правильная обработка ошибок)
    public ResponseEntity<Error> resolvePersistenceException(S3Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Error(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> resolveMessageError(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(e.getMessage()));
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<Error> resolveMessageException(MessageException e) {
        return ResponseEntity.status(e.getStatus()).body(new Error(e.getLocalizedMessage()));
    }
}
