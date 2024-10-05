package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestResetPasswordResModel {
    private Boolean status;
    private String message;
    private Details reset_details;

    @Data
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details {
        private String email;
        private String reset_code;
        private LocalDateTime created_at;
    }
}