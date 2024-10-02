package darts.ng.io.usersMicroservice.darts_app.entity;


import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginReqModel {
    private String email;
    private String password;
}
