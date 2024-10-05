package darts.ng.io.usersMicroservice.darts_app.grpc.grpc_model;


import darts.ng.io.usersMicroservice.darts_app.entity.UserRegistrationReqModel;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailsModel {
    private String  uuid;
    private String token;
    private UserRegistrationReqModel.Details details;
}

