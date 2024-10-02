package darts.ng.io.usersMicroservice.darts_app.entity.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blacklist")
public class UserBlackListedDbModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "users_id")
    private Integer userId;

    private String ip_address;
    private String reason;
    private Boolean is_active;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime expiry_at;

}


