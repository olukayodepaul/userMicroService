package darts.ng.io.usersMicroservice.utilities;


import darts.ng.io.usersMicroservice.darts_app.entity.AddBlacklistEntryReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.ResetPasswordReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.UserLoginReqModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class ValidationUtils {

    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder encoder;

    public ValidationUtils(EmailValidator emailValidator) {
        this.emailValidator = emailValidator;
        this.encoder = new BCryptPasswordEncoder(12);
    }


    public void validateEmail(String email) {
        if (!emailValidator.isValid(email)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error","Invalid email format"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validatePasswordStrength(String password) {

        if (password.length() < 8) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false,"error", "Password must be at least 8 characters long."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must contain at least one uppercase letter."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must contain at least one lowercase letter."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!password.matches(".*\\d.*")) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must contain at least one digit."),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must contain at least one special character (@#$%^&+=!)."),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void isAccountStatusValidated(Boolean condition, String message) {
        if (condition) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", message),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validateRequest(ResetPasswordReqModel request) {
        validateField(request.getReset_code(), "Reset code");
        validateField(request.getNew_password(), "New password");
    }

    public void validateRequestFromBlackList(AddBlacklistEntryReqModel request) {
        validateField(request.getUser_id(), "User Id");
        validateField(request.getReason(), "Reason for blacklisting");
    }

    public void passwordValidateRequest(UserLoginReqModel request) {
        validateField(request.getPassword(), "Password");
    }

    public void validateAccountStatus(Boolean activeStatus, Boolean blackListStatus) {
        isAccountStatusValidated(!activeStatus, "Email not confirmed. Please confirm your email before proceeding.");
        isAccountStatusValidated(blackListStatus, "Account is blacklisted. Please contact customer support.");
    }

    public void validateBlackListExpirationDate(LocalDateTime currentDate, LocalDateTime expirationDate)
    {
        if (currentDate.isBefore(expirationDate)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(
                            false,
                            "Expiration limit exceeded",
                            "Blacklist expire limit not exceeded."),
                    HttpStatus.GONE
            );
        }
    }

    public static void validateField(Object field, String fieldName) {
        if (field == null) {
            throw new CustomRuntimeException(new ErrorHandler(false, "error", fieldName + " cannot be null"), HttpStatus.BAD_REQUEST);
        }
    }

    public void validateMatchPattern(String oldPassword, String newPassword, String comment){
        if(!oldPassword.matches(newPassword)){
            throw new CustomRuntimeException(
                    new ErrorHandler(
                            false,
                            "Pattern not match",
                            comment),
                    HttpStatus.GONE
            );
        }
    }

    //Design an abuse manager to manage the abuse....
    public void validatePassword(String oldPassword, String newPassword) {
        if (encoder.matches(oldPassword, newPassword)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(
                            false,
                            "Password Validation",
                            "Invalid Password, please confirm the password"),
                    HttpStatus.GONE
            );
        }
    }

}