package darts.ng.io.usersMicroservice.login.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq {
    private String email;
    private String password;
}