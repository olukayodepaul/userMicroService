package darts.ng.io.usersMicroservice.registration.service;

import darts.ng.io.usersMicroservice.util.*;
import darts.ng.io.usersMicroservice.registration.entity.RegRequestHandler;
import darts.ng.io.usersMicroservice.registration.entity.Reg;
import darts.ng.io.usersMicroservice.registration.entity.RegistrationResHandler;
import darts.ng.io.usersMicroservice.registration.repository.RegRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class RegistrationImpl {

    private final RegRepo regRepo;
    private final UUIDManager uuidManager;
    private final EmailValidator emailValidator;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

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

    public ResponseEntity<?> registerUser(RegRequestHandler request) {

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

        Reg reg = new Reg();
        reg.setEmail(request.getUser().getEmail());
        reg.setPassword(encoder.encode(request.getUser().getPasswordHash()));
        reg.setUserid(uuidManager.generateUUID(request.getUser().getEmail()));
        reg.setUsername(request.getUser().getUsername());
        Reg result = regRepo.save(reg);

        //Incoming data validation before passing to business logic
        //add bcrypt to the password
        //sign the token
        //send profile data to gRPC failed to respond, then send through kafka.


        //request.getProfile();
        RegistrationResHandler response = new RegistrationResHandler(
                true,
                "User successfully created",
                new RegistrationResHandler.Profile(
                        result.getUserid(),
                        request.getUser().getEmail(),
                        request.getProfile().getFirstName(),
                        request.getProfile().getLastName(),
                        request.getProfile().getGender(),
                        request.getProfile().getDateOfBirth(),
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        request.getProfile().getProfilePictureUrl()
                )
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
