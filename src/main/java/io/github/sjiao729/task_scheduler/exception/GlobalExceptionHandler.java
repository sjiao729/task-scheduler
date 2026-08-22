package io.github.sjiao729.taskscheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    /**
     * When a JobNotFoundException is thrown, return a body with code 404 not 
     * found
     */
    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleJobNotFound( JobNotFoundException ex )
    {
        Map<String, Object> body = new HashMap<>();
        body.put( "timestamp", Instant.now() );
        body.put( "status", HttpStatus.NOT_FOUND.value() );
        body.put( "error", "Not Found" );
        body.put( "message", ex.getMessage() );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * When an invalid argument is used, return a 400 bad request along with 
     * details on which fields are invalid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors( MethodArgumentNotValidException ex )
    {
        Map<String, Object> body = new HashMap<>();
        body.put( "timestamp", Instant.now() );
        body.put( "status", HttpStatus.BAD_REQUEST.value() );
        body.put( "error", "Bad Request" );
        
        Map<String, String> fieldErrors = new HashMap<>();
        // Displays each invalid field
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> fieldErrors.put(fieldError.getField(), 
                fieldError.getDefaultMessage()));
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}