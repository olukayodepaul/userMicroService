package darts.ng.io.usersMicroservice.user_registration_email_confirm.data;


import lombok.*;




@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmRegRes {

    private boolean status;
    private String email;
    private String userId;
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