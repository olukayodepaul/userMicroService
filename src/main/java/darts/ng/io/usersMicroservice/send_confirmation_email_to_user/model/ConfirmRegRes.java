package darts.ng.io.usersMicroservice.send_confirmation_email_to_user.model;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmRegRes {

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