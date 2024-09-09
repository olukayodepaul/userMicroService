package darts.ng.io.usersMicroservice.change_password_on_login.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordOnLoginReq {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
