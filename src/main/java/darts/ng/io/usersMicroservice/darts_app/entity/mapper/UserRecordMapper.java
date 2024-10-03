package darts.ng.io.usersMicroservice.darts_app.entity.mapper;

import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import lombok.*;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRecordMapper {

    private Boolean status;
    private String error;
    private UsersDatabaseModel users;

}
