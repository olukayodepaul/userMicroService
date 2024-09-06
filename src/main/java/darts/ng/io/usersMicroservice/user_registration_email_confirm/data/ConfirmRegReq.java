package darts.ng.io.usersMicroservice.user_registration_email_confirm.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmRegReq {
    private String email;
    private String userId;
}