package co.edu.unisabana.banco.controller;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<?> handleLockConflict(OptimisticLockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicto de concurrencia: intenta de nuevo");
    }
}
