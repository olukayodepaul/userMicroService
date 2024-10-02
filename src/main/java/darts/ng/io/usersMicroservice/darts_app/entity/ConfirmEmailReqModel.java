package darts.ng.io.usersMicroservice.darts_app.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmEmailReqModel {
    private String email;
    private String confirmation_code_link;
}