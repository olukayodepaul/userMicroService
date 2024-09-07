package darts.ng.io.usersMicroservice.send_confirmation_email_to_user.model;

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