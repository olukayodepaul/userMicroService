package darts.ng.io.usersMicroservice.registration.service;

import darts.ng.io.usersMicroservice.util.*;
import darts.ng.io.usersMicroservice.registration.model.RegRequest;
import darts.ng.io.usersMicroservice.registration.model.RegModel;
import darts.ng.io.usersMicroservice.registration.model.ResponseHandler;
import darts.ng.io.usersMicroservice.registration.repository.RegRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import darts.ng.io.usersMicroservice.registration.model.ResponseHandler.UserProfiles;
import darts.ng.io.usersMicroservice.registration.model.ResponseHandler.UserProfiles.UserProfile;
import darts.ng.io.usersMicroservice.registration.model.ResponseHandler.UserProfiles.Users;


@Service
public class RegistrationImpl {

    private final RegRepo regRepo;
    private final UUIDManager uuidManager;
    private final EmailValidator emailValidator;

    public RegistrationImpl(
            RegRepo regRepo,
            UUIDManager uuidManager,
            EmailValidator emailValidator
    )
    {
        this.regRepo = regRepo;
        this.uuidManager = uuidManager;
        this.emailValidator = emailValidator;
    }

    public ResponseEntity<?> registerUser(RegRequest request) {

        if (request == null) {
            throw new CustomException(
                    new RegErrorHandler(false, "Request cannot be null"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!emailValidator.isValid(request.getUser().getEmail())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid email format"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getUser() == null || request.getProfile() == null) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid request data"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (regRepo.existsByEmail(request.getUser().getEmail())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Email already exists"),
                    HttpStatus.CONFLICT
            );
        }

        RegModel reg = new RegModel();
        reg.setEmail(request.getUser().getEmail());
        reg.setPassword(request.getUser().getPasswordHash());
        reg.setUserid(uuidManager.generateUUID(request.getUser().getEmail()));
        RegModel result = regRepo.save(reg);

        //send email notification through a notification manager
        //add bcrypt to the password
        //sign the token
        //send profile data to gRPC failed to respond, then send through kafka.
        //Incoming data validation before passing to business logic

        //request.getProfile();

        ResponseHandler response = new ResponseHandler(
                true,
                "User successfully created",
                new UserProfiles(
                        new Users(
                                request.getUser().getEmail(),
                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                result.getUserid()
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
