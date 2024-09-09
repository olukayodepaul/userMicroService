package darts.ng.io.usersMicroservice.login.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;


@Data
@Entity
@Getter
@Table(name = "users")
public class LoginModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;
    private UUID userid;
    private String password;
    private boolean status;
}
