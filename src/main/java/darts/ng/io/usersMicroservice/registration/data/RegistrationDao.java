package darts.ng.io.usersMicroservice.registration.data;


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

    @Column(name = "user_id", unique = true)
    private UUID userId;

    private String password_hash;
}