package darts.ng.io.usersMicroservice.darts_app.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordOnLoginReqModel {
    private Integer user_id;
    private String old_password;
    private String new_password;
    private String confirm_password;
}