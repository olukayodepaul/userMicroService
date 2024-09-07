package darts.ng.io.usersMicroservice.change_login_password.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


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
