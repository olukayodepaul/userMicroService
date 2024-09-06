package darts.ng.io.usersMicroservice.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<RegErrorHandler> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getResponseHandler(), ex.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<RegErrorHandler> handleIllegalArgumentException(IllegalArgumentException ex) {
        RegErrorHandler errorHandler = new RegErrorHandler(false, "Invalid argument: " + ex.getMessage());
        return new ResponseEntity<>(errorHandler, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<RegErrorHandler> handleGenericException(Exception ex) {
        RegErrorHandler errorHandler = new RegErrorHandler(false, "An unexpected error occurred");
        return new ResponseEntity<>(errorHandler, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}