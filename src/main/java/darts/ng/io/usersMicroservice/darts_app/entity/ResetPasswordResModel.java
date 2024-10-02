package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordResModel {
    private boolean status;
    private String message;
    private Details details;

    @Data
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details {
        private String email;
        private LocalDateTime updated_at;
    }
}
