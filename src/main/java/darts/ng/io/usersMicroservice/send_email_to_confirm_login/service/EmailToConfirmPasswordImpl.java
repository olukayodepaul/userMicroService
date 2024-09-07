package darts.ng.io.usersMicroservice.send_email_to_confirm_login.service;


import darts.ng.io.usersMicroservice.send_email_to_confirm_login.model.EmailToConfirmPasswordModel;
import darts.ng.io.usersMicroservice.send_email_to_confirm_login.model.EmailToConfirmPasswordReq;
import darts.ng.io.usersMicroservice.send_email_to_confirm_login.model.EmailToConfirmPasswordRes;
import darts.ng.io.usersMicroservice.send_email_to_confirm_login.repository.EmailToConfirmPasswordRepo;
import darts.ng.io.usersMicroservice.util.CustomException;
import darts.ng.io.usersMicroservice.util.EmailValidator;
import darts.ng.io.usersMicroservice.util.RegErrorHandler;
import darts.ng.io.usersMicroservice.util.UUIDManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailToConfirmPasswordImpl {

    private final EmailToConfirmPasswordRepo repository;
    private final EmailValidator emailValidator;
    private final UUIDManager uuidManager;

    public EmailToConfirmPasswordImpl(
            EmailToConfirmPasswordRepo repository,
            EmailValidator emailValidator,
            UUIDManager uuidManager
    ) {
        this.repository = repository;
        this.emailValidator = emailValidator;
        this.uuidManager = uuidManager;
    }

    public ResponseEntity<?> sendMail(EmailToConfirmPasswordReq request) {

        if (request == null || request.getEmail() == null) {
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

        Optional<EmailToConfirmPasswordModel> response = repository.findByEmail(request.getEmail());

        if (response.isPresent()) {

            EmailToConfirmPasswordModel isActiveResponse = response.get();
            String resetCode = uuidManager.SixRandomDigitNumberGenerator().toString();
            String resetLink = uuidManager.generateVerificationString();

            isActiveResponse.setResetcode(resetCode);
            isActiveResponse.setResetcodeexpiry(uuidManager.expiryTime(1));
            isActiveResponse.setResetlink(resetLink);

            repository.save(isActiveResponse);

            return new ResponseEntity<>(new EmailToConfirmPasswordRes(
                    true,
                    request.getEmail(),
                    isActiveResponse.getUserid().toString(),
                    "Reset Code sent to the email "+request.getEmail()+" provided",
                    new EmailToConfirmPasswordRes.confirmation(resetCode, resetLink)
            ), HttpStatus.CREATED);
        }

        throw new CustomException(
                new RegErrorHandler(false, "Invalid email provisioning"),
                HttpStatus.BAD_REQUEST
        );
    }
}
