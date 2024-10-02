package darts.ng.io.usersMicroservice.darts_app.entity;


import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserCacheModel;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FetchUserDetailsCacheModel {
    private Boolean status;
    private Integer event;
    private String message;
    private UserCacheModel userDetails;
}
