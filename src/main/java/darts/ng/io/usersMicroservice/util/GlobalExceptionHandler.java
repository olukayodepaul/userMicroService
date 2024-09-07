package darts.ng.io.usersMicroservice.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<RegErrorHandler> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getResponseHandler(), ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
                new RegErrorHandler(false, "An error occurred: Required request data is missing"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


//    @ExceptionHandler(CustomException.class)
//    @ResponseBody
//    public ResponseEntity<RegErrorHandler> handleCustomException(CustomException ex) {
//        return new ResponseEntity<>(ex.getErrorHandler(), ex.getHttpStatus());
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseBody
//    public ResponseEntity<RegErrorHandler> handleIllegalArgumentException(IllegalArgumentException ex) {
//        RegErrorHandler errorHandler = new RegErrorHandler(false, "Invalid argument: " + ex.getMessage());
//        return new ResponseEntity<>(errorHandler, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public ResponseEntity<RegErrorHandler> handleGenericException(Exception ex) {
//        RegErrorHandler errorHandler = new RegErrorHandler(false, "An unexpected error occurred");
//        return new ResponseEntity<>(errorHandler, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

}