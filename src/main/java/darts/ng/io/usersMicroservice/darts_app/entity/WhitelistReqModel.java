package darts.ng.io.usersMicroservice.darts_app.entity;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistReqModel {
    private String reason;
}
