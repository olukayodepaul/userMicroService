package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountReqModel {
    private String email;
    private String password;
    private String role;
    private Integer organisation_id;

    //todo: send this to profileMicroService through kafka or grpc along with userId
    private String first_name;
    private String last_name;
    private String phone_number;
    private String date_of_birth;
    private String gender;
    private String bio;
}