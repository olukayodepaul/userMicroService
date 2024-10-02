package darts.ng.io.usersMicroservice.darts_app.entity.dao;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
@RedisHash("users")
public class UserCacheModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;
    private String user_id;
    private String password;
    private String role;
    private Integer organisation_id;
    private String password_reset_code;
    private String password_reset_expiration;
    private String confirmation_link;
    private String confirmation_code;
    private String confirmation_token_expiration;
    private Boolean is_active;
    private Boolean is_blacklisted;
    private String blacklist_expire_at;
    private String created_at;
    private String updated_at;

}
