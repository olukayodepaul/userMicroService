package darts.ng.io.usersMicroservice.send_confirmation_email_to_user.service;

import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.model.ConfirmRegModel;
import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.model.ConfirmRegReq;
import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.model.ConfirmRegRes;
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
public class ConfirmRegService {

    private final ConfirmRegRepo confirmRegRepo;
    private final EmailValidator emailValidator;
    private final UUIDManager uuidManager;

    public ConfirmRegService(ConfirmRegRepo confirmRegRepo, EmailValidator emailValidator, UUIDManager uuidManager) {
        this.confirmRegRepo = confirmRegRepo;
        this.emailValidator = emailValidator;
        this.uuidManager = uuidManager;
    }

    public ResponseEntity<?> confirmReg(ConfirmRegReq request) {

        if (request == null) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid request data provisioning"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getEmail() == null || request.getUserId() == null) {
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
                new ConfirmRegRes.confirmation(
                        accCode,
                        uid
                )
        ), HttpStatus.CREATED);
    }
}

