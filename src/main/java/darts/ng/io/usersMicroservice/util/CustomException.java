package darts.ng.io.usersMicroservice.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class CustomException  extends RuntimeException {

    private final HttpStatus status;
    private final RegErrorHandler responseHandler;

    public CustomException(RegErrorHandler responseHandler, HttpStatus status) {
        super(responseHandler.getMessage());
        this.status = status;
        this.responseHandler = responseHandler;
    }

}
