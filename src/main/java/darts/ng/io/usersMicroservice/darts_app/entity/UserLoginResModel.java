package darts.ng.io.usersMicroservice.darts_app.entity;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResModel
{
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
        private Integer id;
        private String user_id;
        private String email;
        private String role;
        private Integer organisation_id;
        private Boolean is_active;
        private Boolean is_blacklisted;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;
        private String token;
    }

}
