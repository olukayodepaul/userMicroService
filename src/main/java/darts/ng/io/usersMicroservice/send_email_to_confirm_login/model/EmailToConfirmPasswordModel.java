package darts.ng.io.usersMicroservice.send_email_to_confirm_login.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Getter
@Table(name = "users")
public class EmailToConfirmPasswordModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private UUID userid;
    private String confirmtoken;
    private String confirmcode;
    private LocalDateTime confirmtokenexpire;
}
