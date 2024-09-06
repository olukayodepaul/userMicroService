package darts.ng.io.usersMicroservice.user_registration_email_confirm.service;

import darts.ng.io.usersMicroservice.user_registration_email_confirm.data.ConfirmRegModel;
import darts.ng.io.usersMicroservice.user_registration_email_confirm.data.ConfirmRegReq;
import darts.ng.io.usersMicroservice.user_registration_email_confirm.data.ConfirmRegRes;
import darts.ng.io.usersMicroservice.user_registration_email_confirm.repository.ConfirmRegRepo;
import darts.ng.io.usersMicroservice.util.EmailValidator;
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
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getEmail() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("Invalid request data");
        }

        if (!emailValidator.isValid(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!uuidManager.isValidUUID(request.getUserId())) {
            throw new IllegalArgumentException("Invalid User Id");
        }

        UUID userId = UUID.fromString(request.getUserId());
        ConfirmRegModel response = confirmRegRepo.findByEmailAndUserid((request.getEmail()), userId);

        if (response == null) {
            throw new IllegalArgumentException("Invalid UserId and Email");
        }

        String uid = uuidManager.generateVerificationString();
        String accCode = uuidManager.SixRandomDigitNumberGenerator().toString();

        response.setConfirmtoken(uid);
        response.setConfirmcode(accCode);
        response.setConfirmtokenexpire(uuidManager.expiryDate(7));
        confirmRegRepo.save(response);

        ConfirmRegRes confirm = new ConfirmRegRes (
                true,
                response.getEmail(),
                response.getUserid().toString(),
                new ConfirmRegRes.confirmation(
                    accCode,
                    uid
                )
        );

        return new ResponseEntity<>(confirm, HttpStatus.CREATED);
    }
}

