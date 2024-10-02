package darts.ng.io.usersMicroservice.utilities;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorHandler {
    private boolean status;
    private String error;
    private String message;

}

