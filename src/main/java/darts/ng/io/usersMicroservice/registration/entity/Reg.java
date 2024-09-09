package darts.ng.io.usersMicroservice.registration.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@Getter
@Table(name = "users")
public class Reg implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(unique = true)
    private String email;

    private UUID userid;
    private String password;
    private String username;

}