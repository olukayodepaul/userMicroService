package darts.ng.io.usersMicroservice.registration.service;

import darts.ng.io.usersMicroservice.registration.data.RegErrorHandler;
import darts.ng.io.usersMicroservice.registration.data.RegRequest;
import darts.ng.io.usersMicroservice.registration.data.RegistrationDao;
import darts.ng.io.usersMicroservice.registration.data.ResponseHandler;
import darts.ng.io.usersMicroservice.registration.repository.RegRepo;
import darts.ng.io.usersMicroservice.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import darts.ng.io.usersMicroservice.registration.data.ResponseHandler.UserProfiles;
import darts.ng.io.usersMicroservice.registration.data.ResponseHandler.UserProfiles.UserProfile;
import darts.ng.io.usersMicroservice.registration.data.ResponseHandler.UserProfiles.Users;


@Service
public class RegistrationImpl {

    private final RegRepo regRepo;
    private final UUIDGenerator uuidGenerator;

    public RegistrationImpl(RegRepo regRepo, UUIDGenerator uuidGenerator){
        this.regRepo = regRepo;
        this.uuidGenerator = uuidGenerator;
    }

    public ResponseEntity<?> registerUser(RegRequest request){

        if(regRepo.existsByEmail(request.getUser().getEmail())){
            RegErrorHandler errorHandling = new RegErrorHandler(false, "Email already exists");
            return new ResponseEntity<>(errorHandling, HttpStatus.CONFLICT);
        }

        RegistrationDao reg = new RegistrationDao();
        reg.setEmail(request.getUser().getEmail());
        reg.setPassword_hash(request.getUser().getPasswordHash());
        reg.setUserId(uuidGenerator.generateUUID(request.getUser().getEmail()));
        RegistrationDao result = regRepo.save(reg);

        //send email notification through a notification manager
        //add bcrypt to the password
        //sign the token
        //send profile data to gRPC failed to respond, then send through kafka.

        //request.getProfile();

        ResponseHandler response = new ResponseHandler(
                true,
                "User successfully created",
                new UserProfiles(
                        new Users(
                                request.getUser().getEmail(),
                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                result.getUserId()
                        ),
                        new UserProfile(
                                request.getProfile().getFirstName(),
                                request.getProfile().getLastName(),
                                request.getProfile().getPhoneNumber(),
                                request.getProfile().getAddress(),
                                request.getProfile().getDateOfBirth(),
                                request.getProfile().getBio(),
                                request.getProfile().getProfilePictureUrl()
                        )
                )
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
