package darts.ng.io.usersMicroservice.login.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRes {
    private boolean status;
    private String message;
    private UUID userid;
    private String token;
}