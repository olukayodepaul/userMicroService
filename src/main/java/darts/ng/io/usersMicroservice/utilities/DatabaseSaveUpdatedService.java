package darts.ng.io.usersMicroservice.utilities;

import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class DatabaseSaveUpdatedService {

    private final UserDatabaseRepo databaseRep;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSaveUpdatedService.class);

    public DatabaseSaveUpdatedService(UserDatabaseRepo databaseRep) {
        this.databaseRep = databaseRep;
    }

    public UsersDatabaseModel saveUpdatedUserDetails(UsersDatabaseModel regDetails) {
        try {
            return databaseRep.save(regDetails);
        } catch (Exception e) {
            logger.error("Error saving updated user record: {}", e.getMessage());
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Error saving user details. Please try again."),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
