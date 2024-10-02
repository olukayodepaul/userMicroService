package darts.ng.io.usersMicroservice.darts_app.entity.dao;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UsersDatabaseModel {

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
    private LocalDateTime password_reset_expiration;
    private String confirmation_link;
    private String confirmation_code;
    private LocalDateTime confirmation_token_expiration;
    private Boolean is_active;
    private Boolean is_blacklisted;
    private LocalDateTime blacklist_expire_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
