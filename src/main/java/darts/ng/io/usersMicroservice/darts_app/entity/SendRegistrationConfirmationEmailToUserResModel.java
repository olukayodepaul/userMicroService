package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendRegistrationConfirmationEmailToUserResModel {
    private Boolean status;
    private String message;
    private Details confirmation_details;

    @Data
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details {
        private String email;
        private String confirmation_code;
        private String confirmation_link;
        private LocalDateTime created_at;
    }
}