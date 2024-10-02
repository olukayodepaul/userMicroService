package darts.ng.io.usersMicroservice.darts_app.kafka;


import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import org.springframework.stereotype.Service;

@Service
public class MessageBrokerManager {

    public void PushRegistrationDetailsToCacheMQ(String partition, UserCacheModel cacheModel){

    }

    public void deleteUserDetailsFromCacheMQ(String partition, String email) {

    }

    public void PushProfileToProfileMicroMServiceMQ(UsersDatabaseModel fetchedSavedRecordFromDB){

        //todo: send this to profileMicroService through kafka or grpc along with userId
        //    private String first_name;
        //    private String last_name;
        //    private String phone_number;
        //    private String date_of_birth;
        //    private String gender;
        //    private String bio;
        //   private Integer id;
    }

    public void updateUserDetailsThroughMQ(String partition, UserCacheModel cacheModel) {

    }

    public void passwordRequestRateLimitMQ(String email){

    }


}
