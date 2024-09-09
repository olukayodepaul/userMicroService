package darts.ng.io.usersMicroservice.change_password_on_login.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordOnLoginRes {
    private Boolean status;
    private String message;
}