package darts.ng.io.usersMicroservice.change_login_password.service;

import darts.ng.io.usersMicroservice.change_login_password.entity.ChangePasswordModel;
import darts.ng.io.usersMicroservice.change_login_password.entity.ChangePasswordReq;
import darts.ng.io.usersMicroservice.change_login_password.entity.ChangePasswordRes;
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

    public ChangePasswordImpl(
            ChangePasswordRepo repository,
            EmailValidator emailValidator
    ) {
        this.repository = repository;
        this.emailValidator = emailValidator;
    }

    public ResponseEntity<?> changePassword(ChangePasswordReq request) {

        if (request == null || request.getEmail() == null || request.getNewPassword() == null) {
            throw new CustomException(
                    new RegErrorHandler(false, "Missing required fields."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!emailValidator.isValid(request.getEmail())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid email format."),
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
                        new RegErrorHandler(false, "Reset link or code is invalid."),
                        HttpStatus.FORBIDDEN
                );
            }

            // Check if the reset code has expired
            if (isActiveResponse.getResetcodeexpiry() == null || isActiveResponse.getResetcodeexpiry().isBefore(LocalDateTime.now())) {
                throw new CustomException(
                        new RegErrorHandler(false, "Reset code has expired."),
                        HttpStatus.GONE
                );
            }

            // Check if the reset code matches
            if (!request.getAccessCode().equalsIgnoreCase(isActiveResponse.getResetlink()) &&
                    !request.getAccessCode().equalsIgnoreCase(isActiveResponse.getResetcode())) {
                return new ResponseEntity<>(
                        new RegErrorHandler(false, "Invalid reset code."),
                        HttpStatus.BAD_REQUEST
                );
            }

            isActiveResponse.setResetcode(null);
            isActiveResponse.setResetcodeexpiry(null);
            isActiveResponse.setResetlink(null);
            isActiveResponse.setPassword(request.getNewPassword()); // Enter the bcrypt password here
            repository.save(isActiveResponse);

            return new ResponseEntity<>(new ChangePasswordRes(
                    true,
                    "Password updated successfully."
            ), HttpStatus.OK);
        }

        throw new CustomException(
                new RegErrorHandler(false, "Email not found."),
                HttpStatus.NOT_FOUND
        );
    }



}
