package darts.ng.io.usersMicroservice.change_password_on_login.servive;

import darts.ng.io.usersMicroservice.change_password_on_login.entity.ChangePasswordOnLogin;
import darts.ng.io.usersMicroservice.change_password_on_login.entity.ChangePasswordOnLoginReq;
import darts.ng.io.usersMicroservice.change_password_on_login.repository.ChangePasswordOnLoginDao;
import darts.ng.io.usersMicroservice.util.CustomException;
import darts.ng.io.usersMicroservice.util.EmailValidator;
import darts.ng.io.usersMicroservice.util.RegErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ChangePasswordOnLoginImpl {

    private final ChangePasswordOnLoginDao databaseDao;
    private final EmailValidator emailValidator;

    public ChangePasswordOnLoginImpl(ChangePasswordOnLoginDao databaseDao, EmailValidator emailValidator) {
        this.databaseDao = databaseDao;
        this.emailValidator = emailValidator;
    }


    public ResponseEntity<?> onChangePassword(ChangePasswordOnLoginReq request) {

        if (request == null
                || request.getEmail() == null
                || request.getNewPassword() == null
                || request.getConfirmPassword() == null
                || request.getOldPassword() == null
        ) {
            throw new CustomException(
                    new RegErrorHandler(false, "Missing required fields."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!emailValidator.isValid(request.getEmail())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid email provisioning"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(
                    new RegErrorHandler(false, "password did  not match"),
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<ChangePasswordOnLogin> response = databaseDao.findByEmail(request.getEmail());

        if (response.isPresent()) {

            ChangePasswordOnLogin isActiveResponse = response.get();

            if (!isActiveResponse.getPassword().equals(request.getOldPassword())) {
                throw new CustomException(
                        new RegErrorHandler(false, "Invalid password. Please try again."),
                        HttpStatus.UNAUTHORIZED
                );
            }

        }

        throw new CustomException(
                new RegErrorHandler(false, "Invalid email provisioning"),
                HttpStatus.BAD_REQUEST
        );

    }
}