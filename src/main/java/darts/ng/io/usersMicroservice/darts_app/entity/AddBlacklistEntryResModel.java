package darts.ng.io.usersMicroservice.darts_app.entity;


import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddBlacklistEntryResModel {
    private Boolean status;
    private String message;
    private Integer blacklist_id;
}
