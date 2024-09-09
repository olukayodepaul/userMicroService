package darts.ng.io.usersMicroservice.change_password_on_login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
@Entity
@Getter
@Table(name = "users")
public class ChangePasswordOnLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private String password;
    private boolean status;

}