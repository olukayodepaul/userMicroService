package darts.ng.io.usersMicroservice.darts_app.service.user_account_service;


import darts.ng.io.usersMicroservice.darts_app.entity.UserRegistrationConfirmationReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.mapper.UserRecordMapper;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.utilities.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserRegistrationConfirmationServiceImpl {


    private final UserDatabaseRepo updateRepo;
    private final UtilitiesManager utilitiesManager;
    private final ValidationUtils validationUtils;
    private final DbSaveUpdatedService dbSaveUpdatedService;

    /**
     * Constructor for UserRegistrationEmailConfirmationServiceImpl.
     *
     * @param updateRepo          Repository for updating user data.
     * @param utilitiesManager    Utility manager for generating tokens, strings, etc.
     * @param validationUtils     Utility class for validating emails and user statuses.
     * @param dbSaveUpdatedService Service for saving and updating user records in the database.
     */
    public UserRegistrationConfirmationServiceImpl(
            UserDatabaseRepo updateRepo,
            UtilitiesManager utilitiesManager,
            ValidationUtils validationUtils,
            DbSaveUpdatedService dbSaveUpdatedService
    ) {
        this.updateRepo = updateRepo;
        this.utilitiesManager = utilitiesManager;
        this.validationUtils = validationUtils;
        this.dbSaveUpdatedService = dbSaveUpdatedService;
    }

    public ResponseEntity<ResponseHandler> confirmRegEmail(UserRegistrationConfirmationReqModel request) {

        validationUtils.validateEmailConfirmationCode(request);
        validationUtils.sanitizeEmail(request.getEmail());

        // Check if the user exists
        UsersDatabaseModel existingUser = updateRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, "validation check", "Email does not exist"),
                        HttpStatus.NOT_FOUND
                ));


        validationUtils.validateAccountStatus(existingUser.getIs_active(), existingUser.getIs_blacklisted());
        validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), existingUser.getBlacklist_expire_at());

        if (!existingUser.getConfirmation_code().equalsIgnoreCase(request.getConfirmation_code_link()) &&
                !existingUser.getConfirmation_link().equalsIgnoreCase(request.getConfirmation_code_link())) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "validation check", "Invalid confirmation code or link. Please try again."),
                    HttpStatus.BAD_REQUEST
            );
        }

        UsersDatabaseModel updateUser = updateUser(existingUser);

        UserRecordMapper saveResult = dbSaveUpdatedService.saveUpdatedUserRecord(updateUser);

        if (!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "We can't confirm the email at this moment", saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseHandler(true, "Email successful confirm"));
    }

    /**
     * Updates the user model with a new confirmation link, token, and expiration time.
     *
     * @param request The user data to be updated.
     * @return UsersDatabaseModel containing updated user details.
     */
    private UsersDatabaseModel updateUser(UsersDatabaseModel request) {
        UUID emailToUUID = utilitiesManager.generateUUID(request.getEmail());
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime initialDate = utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00");
        return UsersDatabaseModel.builder()
                .id(request.getId())
                .uuid(request.getUuid())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .organisation_id(request.getOrganisation_id())
                .password_reset_code(request.getPassword_reset_code())
                .password_reset_expiration(request.getPassword_reset_expiration())
                .confirmation_link("")
                .confirmation_code("")
                .confirmation_token_expiration(initialDate)
                .is_active(request.getIs_active())
                .is_blacklisted(request.getIs_blacklisted())
                .password_reset_expiration(request.getPassword_reset_expiration())
                .created_at(request.getCreated_at())
                .updated_at(dateTime)
                .build();
    }
}
