package darts.ng.io.usersMicroservice.change_login_password.service;

import darts.ng.io.usersMicroservice.change_login_password.model.ChangePasswordModel;
import darts.ng.io.usersMicroservice.change_login_password.model.ChangePasswordReq;
import darts.ng.io.usersMicroservice.change_login_password.model.ChangePasswordRes;
import darts.ng.io.usersMicroservice.change_login_password.repository.ChangePasswordRepo;
import darts.ng.io.usersMicroservice.util.CustomException;
import darts.ng.io.usersMicroservice.util.EmailValidator;
import darts.ng.io.usersMicroservice.util.RegErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class ChangePasswordImpl {

    private final ChangePasswordRepo repository;
    private final EmailValidator emailValidator;

    public ChangePasswordImpl(ChangePasswordRepo repository, EmailValidator emailValidator) {
        this.repository = repository;
        this.emailValidator = emailValidator;
    }

    public ResponseEntity<?> changePassword(ChangePasswordReq request) {

        if (request == null || request.getEmail() == null || request.getNewPassword() == null) {
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

        Optional<ChangePasswordModel> response = repository.findByEmail(request.getEmail());

        if (response.isPresent()) {

            ChangePasswordModel isActiveResponse = response.get();

            if (isActiveResponse.getResetlink() == null
                    || isActiveResponse.getResetcode() == null
                    || isActiveResponse.getResetcode().isEmpty()
                    || isActiveResponse.getResetlink().isEmpty()
                    || isActiveResponse.getResetlink().isBlank()
                    || isActiveResponse.getResetcode().isBlank()
            ) {
                throw new CustomException(
                        new RegErrorHandler(false, "Verification code no longer valid."),
                        HttpStatus.UNAUTHORIZED
                );
            }

            //check if the reset code has expired
            if (isActiveResponse.getResetcodeexpiry() == null || isActiveResponse.getResetcodeexpiry().isBefore(LocalDateTime.now())) {
                throw new CustomException(
                        new RegErrorHandler(false, "Verification code has expired."),
                        HttpStatus.UNAUTHORIZED
                );
            }

            //check the set code if matches what is in the db
            if (!request.getAccessCode().equalsIgnoreCase(isActiveResponse.getResetlink()) &&
                    !request.getAccessCode().equalsIgnoreCase(isActiveResponse.getResetcode())) {
                return new ResponseEntity<>(
                        new RegErrorHandler(false, "Invalid reset code. Please try again."),
                        HttpStatus.BAD_REQUEST
                );
            }


            isActiveResponse.setResetcode(null);
            isActiveResponse.setResetcodeexpiry(null);
            isActiveResponse.setResetlink(null);
            isActiveResponse.setPassword(request.getNewPassword()); //enter the bcrypt password here
            repository.save(isActiveResponse);

            return new ResponseEntity<>(new ChangePasswordRes(
                   true,
                    "Password Successful Updated"
            ), HttpStatus.CREATED);
        }

        throw new CustomException(
                new RegErrorHandler(false, "Invalid email provisioning"),
                HttpStatus.BAD_REQUEST
        );
    }
}
