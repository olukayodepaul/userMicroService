package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendUserConfirmEmailReqModel {
    private String email;
}