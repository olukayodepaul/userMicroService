package darts.ng.io.usersMicroservice.change_login_password.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;


@Data
@Entity
@Getter
@Table(name = "users")
public class ChangePasswordModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private String password;
    private String resetcode;
    private String resetlink;
    private LocalDateTime resetcodeexpiry;

}
