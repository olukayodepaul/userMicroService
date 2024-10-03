package darts.ng.io.usersMicroservice.utilities;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class CustomRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final HttpStatus status;
    private final ErrorHandler responseHandler;

    public CustomRuntimeException(ErrorHandler responseHandler, HttpStatus status) {
        super(responseHandler.getMessage());
        this.status = status;
        this.responseHandler = responseHandler;
    }

    @Override
    public String toString() {
        return "CustomRuntimeException{" +
                "status=" + status +
                ", responseHandler=" + responseHandler +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
