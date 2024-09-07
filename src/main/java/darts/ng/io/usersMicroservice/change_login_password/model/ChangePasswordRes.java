package darts.ng.io.usersMicroservice.change_login_password.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRes {
    private Boolean status;
    private String message;
}