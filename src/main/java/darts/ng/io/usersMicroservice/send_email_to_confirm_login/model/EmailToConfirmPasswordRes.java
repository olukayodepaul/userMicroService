package darts.ng.io.usersMicroservice.send_email_to_confirm_login.model;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailToConfirmPasswordRes {

    private boolean status;
    private String email;
    private String userId;
    private String message;
    private confirmation access;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class confirmation {
        private String code;
        private String link;
    }
}