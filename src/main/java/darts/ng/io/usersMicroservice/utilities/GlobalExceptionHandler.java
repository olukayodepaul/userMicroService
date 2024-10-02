package darts.ng.io.usersMicroservice.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<ErrorHandler> handleCustomException(CustomRuntimeException ex) {
        return new ResponseEntity<>(ex.getResponseHandler(), ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
                new ErrorHandler(false, "Error",ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}