package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlackListedResponseModel {
    private UUID uuid;
    private String ip_address;
    private String reason;
    private Boolean is_active;
    private LocalDateTime created_at;
    private LocalDateTime expiry_at;

}