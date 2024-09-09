package darts.ng.io.usersMicroservice.send_email_to_confirm_login.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Getter
@Table(name = "users")
public class Database {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private UUID userid;
    private String resetcode;
    private String resetlink;
    private LocalDateTime resetcodeexpiry;
}
