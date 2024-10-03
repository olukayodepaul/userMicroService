package darts.ng.io.usersMicroservice.darts_app.kafka;


import darts.ng.io.usersMicroservice.darts_app.entity.UserRegistrationReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import org.springframework.stereotype.Service;

@Service
public class MessageBrokerManager {

    //used
    public void pushProfileToProfileMicroMServiceMQ(UserRegistrationReqModel request){}

    public void PushRegistrationDetailsToCacheMQ(String partition, UserCacheModel cacheModel){

    }

    public void deleteUserDetailsFromCacheMQ(String partition, String email) {

    }

    public void updateUserDetailsThroughMQ(String partition, UsersDatabaseModel cacheModel) {

    }

    public void passwordRequestRateLimitMQ(String email){

    }

}
