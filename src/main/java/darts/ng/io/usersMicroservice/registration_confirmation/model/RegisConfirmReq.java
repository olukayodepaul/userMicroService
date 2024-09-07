package darts.ng.io.usersMicroservice.registration_confirmation.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisConfirmReq {
    private String email;
    private  String userId;
    private  String accessCode;
}
