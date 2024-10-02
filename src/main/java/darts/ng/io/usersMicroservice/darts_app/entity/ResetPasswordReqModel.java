package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordReqModel {
    private String email;
    private String reset_code;
    private String new_password;
}