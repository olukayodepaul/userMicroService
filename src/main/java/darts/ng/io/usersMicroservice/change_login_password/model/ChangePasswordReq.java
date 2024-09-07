package darts.ng.io.usersMicroservice.change_login_password.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordReq {
    private String email;
    private String newPassword;
    private String accessCode;
}
