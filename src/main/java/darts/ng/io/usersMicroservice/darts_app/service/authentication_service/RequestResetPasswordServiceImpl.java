package darts.ng.io.usersMicroservice.darts_app.service.authentication_service;

import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.rate_limit.RateLimitService;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class RequestResetPasswordServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(RequestResetPasswordServiceImpl.class);
    private final UserDatabaseRepo databaseRep;
    private final UtilitiesManager utilitiesManager;
    private final ValidationUtils validationUtils;
    private final DatabaseSaveUpdatedService databaseSaveUpdatedService;
    private final BCryptPasswordEncoder encoder;
    private final RateLimitService rateLimitService;


    public RequestResetPasswordServiceImpl(
            UserDatabaseRepo databaseRep,
            UtilitiesManager utilitiesManager,
            ValidationUtils validationUtils,
            DatabaseSaveUpdatedService databaseSaveUpdatedService,
            RateLimitService rateLimitService
    ) {
        this.databaseRep = databaseRep;
        this.utilitiesManager = utilitiesManager;
        this.validationUtils = validationUtils;
        this.databaseSaveUpdatedService = databaseSaveUpdatedService;
        this.encoder = new BCryptPasswordEncoder(12);
        this.rateLimitService = rateLimitService;
    }

    /**
     * Handles the request for password reset. Validates the email, checks account status,
     * and generates a password reset code.
     *
     * @param request the request model containing the user's email
     * @return a ResponseEntity containing the result of the operation
     * @throws CustomRuntimeException if email validation fails or the account is inactive or blacklisted
     */
    public ResponseEntity<RequestResetPasswordResModel> requestPasswordReset(RequestResetPasswordReqModel request) {

        //rate limit service
        if(rateLimitService.isRateLimited(request.getEmail())){
            throw new CustomRuntimeException(
                    new ErrorHandler(false,
                            "Rate limit exceeded",
                            "You have exceeded the maximum number of requests per minute."
                    ),
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }

        validationUtils.validateEmail(request.getEmail());

        // Retrieve user by email
        Optional<UsersDatabaseModel> userByEmail = databaseRep.findByEmail(request.getEmail());

        if (userByEmail.isPresent()) {

            UsersDatabaseModel existingUser = userByEmail.get();

            // Validate the account's status (active and not blacklisted)
            validationUtils.validateAccountStatus(existingUser.getIs_active(), existingUser.getIs_blacklisted());
            validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), existingUser.getBlacklist_expire_at());


            String plainPassword  = utilitiesManager.SixRandomDigitNumberGenerator().toString();
            String encodedPassword = encoder.encode(plainPassword);

            // Build updated user details with password reset info
            UsersDatabaseModel dbModelBuilder = UsersDatabaseModel.builder()
                    .id(existingUser.getId())
                    .user_id(existingUser.getUser_id())
                    .email(existingUser.getEmail())
                    .password(existingUser.getPassword())
                    .role(existingUser.getRole())
                    .organisation_id(existingUser.getOrganisation_id())
                    .password_reset_code(encodedPassword)
                    .password_reset_expiration(utilitiesManager.expiryDatePeriod(20)) //change to global seconds
                    .confirmation_link(existingUser.getConfirmation_link())
                    .confirmation_code(existingUser.getConfirmation_code())
                    .confirmation_token_expiration(existingUser.getConfirmation_token_expiration())
                    .is_active(existingUser.getIs_active())
                    .is_blacklisted(existingUser.getIs_blacklisted())
                    .blacklist_expire_at(existingUser.getBlacklist_expire_at())
                    .created_at(existingUser.getCreated_at())
                    .updated_at(existingUser.getUpdated_at())
                    .build();

            // Save the updated user record
            UsersDatabaseModel savedUser = databaseSaveUpdatedService.saveUpdatedUserDetails(dbModelBuilder);

            // Return the response with the reset code and a message to use an external service for notifications
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new RequestResetPasswordResModel(
                            true,
                            "Password reset code generated successfully. Please send this code via your email service.",
                            new RequestResetPasswordResModel.Details(
                                    savedUser.getEmail(),
                                    savedUser.getUser_id(),
                                    plainPassword,
                                    savedUser.getUpdated_at()
                            )
                    ));
        }

        // Throw an error if the email is not found in the system
        throw new CustomRuntimeException(
                new ErrorHandler(false,
                        "invalid email",
                        "Kindly provide valid email address"),
                HttpStatus.BAD_REQUEST
        );
    }
}
