package darts.ng.io.usersMicroservice.registration.data;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegErrorHandler {
    private boolean status;
    private String message;
}

