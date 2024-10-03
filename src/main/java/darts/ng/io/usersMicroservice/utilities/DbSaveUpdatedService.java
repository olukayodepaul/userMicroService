package darts.ng.io.usersMicroservice.utilities;

import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.mapper.UserRecordMapper;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class DbSaveUpdatedService {

    private final UserDatabaseRepo databaseRep;
    private static final Logger logger = LoggerFactory.getLogger(DbSaveUpdatedService.class);

    public DbSaveUpdatedService(UserDatabaseRepo databaseRep) {
        this.databaseRep = databaseRep;
    }

    public UserRecordMapper saveUpdatedUserRecord(UsersDatabaseModel regDetails) {
        try {
            return new UserRecordMapper(true, "", databaseRep.save(regDetails)) ;
        } catch (Exception e) {
            logger.error("DbSaveUpdatedService::saveUpdatedUserDetails: {}", e.getMessage());
            return new UserRecordMapper(false, e.getMessage(), UsersDatabaseModel.builder().build());
        }
    }


}
