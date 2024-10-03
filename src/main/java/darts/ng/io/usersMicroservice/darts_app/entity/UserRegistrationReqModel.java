package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationReqModel {

    private String email;
    private String password;
    private String role;
    private Integer organisation_id;
    private Details details;

    @Data
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details {
        private String first_name;
        private String last_name;
        private String phone_number;
        private String date_of_birth;
        private String gender;
        private String bio;
    }

    private String uuid;

}
