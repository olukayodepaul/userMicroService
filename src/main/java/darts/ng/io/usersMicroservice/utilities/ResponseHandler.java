package darts.ng.io.usersMicroservice.utilities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseHandler {
    private boolean status;
    private String message;
}
