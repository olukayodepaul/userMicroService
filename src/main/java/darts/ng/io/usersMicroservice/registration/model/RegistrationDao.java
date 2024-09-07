package darts.ng.io.usersMicroservice.registration.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
@Entity
@Getter
@Table(name = "users")
public class RegistrationDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    private UUID userid;
    private String password_hash;
}