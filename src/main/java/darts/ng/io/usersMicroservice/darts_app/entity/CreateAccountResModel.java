package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountResModel {
    private Boolean status;
    private String message;
    private Details user_details;

    @Data
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details {
        private String email;
        private String user_id;
        private String role;
        private String first_name;
        private String last_name;
        private String phone_number;
        private String date_of_birth;
        private String gender;
        private String bio;
        private Integer organisation_id;
        private Boolean is_active;
        private Boolean is_blacklisted;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;
    }

}