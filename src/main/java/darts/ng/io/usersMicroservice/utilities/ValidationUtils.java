package darts.ng.io.usersMicroservice.utilities;


import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.rate_limit.BruteForceRateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class ValidationUtils {

    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder encoder;
    private final BruteForceRateLimitService rateLimitService;

    public ValidationUtils(
            EmailValidator emailValidator,
            BruteForceRateLimitService rateLimitService
    ) {
        this.emailValidator = emailValidator;
        this.encoder = new BCryptPasswordEncoder(12);
        this.rateLimitService = rateLimitService;
    }


    public void sanitizeEmail(String email) {
        if (!emailValidator.isValid(email)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Invalid email format"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validatePasswordStrength(String password) {

        if (password.length() < 8) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", "Password must be at least 8 characters long."),
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
                    new ErrorHandler(false, "message", message),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validateEmailConfirmationCode(UserRegistrationConfirmationReqModel request) {
        validateField(request.getEmail(), "Email");
        validateField(request.getConfirmation_code_link(), "Confirmation code");
    }

    public void validateRequest(ResetPasswordReqModel request) {
        validateField(request.getReset_code(), "Reset code");
        validateField(request.getNew_password(), "New password");
    }

    public void validateRequestFromBlackList(AddBlacklistEntryReqModel request) {
        validateField(request.getPeriod_in_second(), "Period in second");
        validateField(request.getReason(), "Reason for blacklisting");
    }

    public void passwordValidateRequest(UserLoginReqModel request) {
        validateField(request.getPassword(), "Password");
    }

    public void validateAccountStatus(Boolean activeStatus, Boolean blackListStatus) {
        isAccountStatusValidated(activeStatus, "Account already confirm. Please proceeding to login.");
        isAccountStatusValidated(blackListStatus, "Account is blacklisted. Please contact customer support.");
    }

    public void validateAccountNotConfirm(Boolean activeStatus) {
        if(!activeStatus){
            throw new CustomRuntimeException(new ErrorHandler(false, "Email Not Confirm",  "Please confirm your email before proceeding."), HttpStatus.BAD_REQUEST);
        }
    }

    public void validateAccountBlackListed(Boolean activeStatus) {
        if(activeStatus){
            throw new CustomRuntimeException(new ErrorHandler(false, "Account Black listed",  "Account on suspension"), HttpStatus.BAD_REQUEST);
        }
    }

    public void emailConfirmationExpiration(LocalDateTime expirationDate) {
        if(LocalDateTime.now().isAfter(expirationDate)) {
            throw new CustomRuntimeException(new ErrorHandler(false, "Reset",  "Exceed email confirmation period, kindly reconfirm"), HttpStatus.BAD_REQUEST);
        }
    }

    public void blackListExpiration(LocalDateTime expirationDate) {
        if(LocalDateTime.now().isBefore(expirationDate)){
            throw new CustomRuntimeException(new ErrorHandler(false, "Black listed",  "Account on suspension. Suspension will be lifted "+ expirationDate), HttpStatus.BAD_REQUEST);
        }
    }

    public void tokenValidateRequest(String token) {
        validateField(token, "Token");
    }

    public void reasonValidateRequest(String reason) {
        validateField(reason, "Reason");
    }

    public void userValidateRequest(UserRegistrationReqModel request) {
        validateField(request.getPassword(), "Password");
        validateField(request.getRole(), "User Role");
        validateField(request.getOrganisation_id(), "Organisation ID");
        validateField(request.getDetails().getFirst_name(), "First Name");
        validateField(request.getDetails().getLast_name(), "Last Name");
        validateField(request.getDetails().getPhone_number(), "Phone Number");
        validateField(request.getDetails().getDate_of_birth(), "Date of Birth");
        validateField(request.getDetails().getGender(), "Gender");
        validateField(request.getDetails().getBio(), "Bio");
    }

    public static void validateField(Object field, String fieldName) {
        if (field == null) {
            throw new CustomRuntimeException(new ErrorHandler(false, "validation error", fieldName + " cannot be null"), HttpStatus.BAD_REQUEST);
        }
    }

    //old password is the one from the db (encrypted)
    //new password is the one from the request (none encrypted)
    public void validateMatchPattern(String oldPassword, String newPassword, String comment) {
        if (!oldPassword.matches(newPassword)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(
                            false,
                            "Pattern not match",
                            comment),
                    HttpStatus.GONE
            );
        }
    }

    public void validateConfirmationCodeAndLink(String confirmationCodeOrLink, String confirmationCode, String confirmationLink) {
        if (!encoder.matches(confirmationCodeOrLink, confirmationCode) && !confirmationCodeOrLink.equalsIgnoreCase(confirmationLink)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "validation check", "Invalid confirmation code or link. Please try again."),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validateResetCode(String plainTextPassword, String encodedPassword) {
        if (!encoder.matches(plainTextPassword, encodedPassword)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(
                            false,
                            "Reset",
                            "Invalid reset code, please provide valid reset code"),
                    HttpStatus.GONE
            );
        }
    }

    public void checkResetPasswordExpiration(LocalDateTime expirationDate) {
        if(LocalDateTime.now().isAfter(expirationDate)) {
            throw new CustomRuntimeException(new ErrorHandler(false, "Reset",  "Reset code exceed expected duration."), HttpStatus.BAD_REQUEST);
        }
    }

    public void validatePassword(String plainTextPassword, String encodedPassword) {
        if (!encoder.matches(plainTextPassword, encodedPassword)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(
                            false,
                            "Password Validation",
                            "Invalid Password, please confirm the password"),
                    HttpStatus.GONE
            );
        }
    }

    public void validateOldWithNewPassword(String plainTextPassword, String encodedPassword) {
        if (!encoder.matches(plainTextPassword, encodedPassword)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(
                            false,
                            "Password not match",
                            "new password do not match the old password"),
                    HttpStatus.GONE
            );
        }
    }

    public String plainTextEncryption(String password) {
        return encoder.encode(password);
    }

    //brute for attack limit
    public void bruteForceProtection(String email, String types) {
        if (rateLimitService.isRateLimited(email, types)) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false,
                            "Rate limit exceeded",
                            "You have exceeded the maximum number of requests per minute."
                    ),
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }
    }

}