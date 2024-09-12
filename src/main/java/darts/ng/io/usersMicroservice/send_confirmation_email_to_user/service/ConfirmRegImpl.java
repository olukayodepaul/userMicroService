package darts.ng.io.usersMicroservice.send_confirmation_email_to_user.service;

import darts.ng.io.usersMicroservice.security.JwtService;
import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.entity.ConfirmRegModel;
import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.entity.ConfirmRegReq;
import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.entity.ConfirmRegRes;
import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.repository.ConfirmRegRepo;
import darts.ng.io.usersMicroservice.util.CustomException;
import darts.ng.io.usersMicroservice.util.EmailValidator;
import darts.ng.io.usersMicroservice.util.RegErrorHandler;
import darts.ng.io.usersMicroservice.util.UUIDManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ConfirmRegImpl {

    private final ConfirmRegRepo confirmRegRepo;
    private final EmailValidator emailValidator;
    private final UUIDManager uuidManager;
    private final JwtService jwtService;

    public ConfirmRegImpl(
            ConfirmRegRepo confirmRegRepo,
            EmailValidator emailValidator,
            UUIDManager uuidManager,
            JwtService jwtService) {
        this.confirmRegRepo = confirmRegRepo;
        this.emailValidator = emailValidator;
        this.uuidManager = uuidManager;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> confirmReg(ConfirmRegReq request, String authHeader) {

        System.out.println(jwtService.extractEmail(authHeader.substring(7)));

        if (request == null || request.getEmail() == null || request.getUserId() == null) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid request data provisioning"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!emailValidator.isValid(request.getEmail())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid email provisioning"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!uuidManager.isValidUUID(request.getUserId())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid user ID provisioning"),
                    HttpStatus.BAD_REQUEST
            );
        }

        UUID userId = UUID.fromString(request.getUserId());
        ConfirmRegModel response = confirmRegRepo.findByEmailAndUserid((request.getEmail()), userId);

        if (response == null) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid email provisioning"),
                    HttpStatus.BAD_REQUEST
            );
        }

        String uid = uuidManager.generateVerificationString();
        String accCode = uuidManager.SixRandomDigitNumberGenerator().toString();

        response.setConfirmtoken(uid);
        response.setConfirmcode(accCode);
        response.setConfirmtokenexpire(uuidManager.expiryDate(7));
        confirmRegRepo.save(response);

        return new ResponseEntity<>(new ConfirmRegRes (
                true,
                response.getEmail(),
                response.getUserid().toString(),
                "Confirmation Code sent to the email "+request.getEmail()+" provided",
                new ConfirmRegRes.confirmation(
                        accCode,
                        uid
                )
        ), HttpStatus.CREATED);
    }
}

