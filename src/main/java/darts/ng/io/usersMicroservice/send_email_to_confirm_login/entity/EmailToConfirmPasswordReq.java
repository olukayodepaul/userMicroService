package darts.ng.io.usersMicroservice.send_email_to_confirm_login.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailToConfirmPasswordReq {
    private String email;
}
