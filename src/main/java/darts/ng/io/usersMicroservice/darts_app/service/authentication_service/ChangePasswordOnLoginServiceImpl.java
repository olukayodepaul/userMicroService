package darts.ng.io.usersMicroservice.darts_app.service.authentication_service;


import darts.ng.io.usersMicroservice.darts_app.entity.ChangePasswordOnLoginReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.ChangePasswordOnLoginResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.FetchUserDetailsCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.mapper.UserRecordMapper;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.security.JwtService;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class ChangePasswordOnLoginServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordOnLoginServiceImpl.class);
    private final UserDatabaseRepo databaseRep;
    private final ValidationUtils validationUtils;
    private final DbSaveUpdatedService dbSaveUpdatedService;
    private final BCryptPasswordEncoder encoder;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final JwtService jwtService;

    public ChangePasswordOnLoginServiceImpl(
            UserDatabaseRepo databaseRep,
            ValidationUtils validationUtils,
            DbSaveUpdatedService dbSaveUpdatedService,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager,
            JwtService jwtService
    ) {
        this.databaseRep = databaseRep;
        this.validationUtils = validationUtils;
        this.dbSaveUpdatedService = dbSaveUpdatedService;
        this.encoder = new BCryptPasswordEncoder(12);
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.jwtService = jwtService;
    }

    public ResponseEntity<ChangePasswordOnLoginResModel> changePasswordOnLogin(ChangePasswordOnLoginReqModel request, String token) {

        validationUtils.bruteForceProtection(jwtService.extractEmail(token), AppConfig.CHANGE_PASSWORD_ON_LOGIN_LIMIT);
        validationUtils.validateMatchPattern(request.getNew_password(), request.getConfirm_password(), AppConfig.CHANGE_PASSWORD_NOT_MATCH);
        validationUtils.validatePasswordStrength(request.getNew_password());

        // Check if the user exists
        UsersDatabaseModel existingUser = databaseRep.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.KAY_ERROR, AppConfig.INVALID_UUID),
                        HttpStatus.NOT_FOUND
                ));

        validationUtils.validateAccountNotConfirm(existingUser.getIs_active());
        validationUtils.validateAccountBlackListed(existingUser.getIs_blacklisted());
        validationUtils.blackListExpiration(existingUser.getBlacklist_expire_at());
        validationUtils.validateOldWithNewPassword(request.getOld_password(), existingUser.getPassword());

        UsersDatabaseModel updateUser = updatePassword(existingUser, request.getNew_password());
        UserRecordMapper saveResult = dbSaveUpdatedService.saveUpdatedUserRecord(updateUser);

        if (!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "We can't confirm the uuid at this moment", saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        FetchUserDetailsCacheModel deleteCachedUser = cacheService.deleteUserDetails(updateUser.getEmail());

        if (deleteCachedUser.getStatus()) {
            messageBrokerManager.deleteUserDetailsFromCacheMQ("delete", updateUser.getEmail());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Updates the user model with a new confirmation link, token, and expiration time.
     *
     * @param user The user data to be updated.
     * @param newPassword the new changed password
     * @return UsersDatabaseModel containing updated user details.
     */
    private UsersDatabaseModel updatePassword(UsersDatabaseModel user, String newPassword) {
        return UsersDatabaseModel.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .password(encoder.encode(newPassword))
                .role(user.getRole())
                .organisation_id(user.getOrganisation_id())
                .password_reset_code(user.getPassword_reset_code())
                .password_reset_expiration(user.getPassword_reset_expiration())
                .confirmation_link(user.getConfirmation_link())
                .confirmation_code(user.getConfirmation_code())
                .confirmation_token_expiration(user.getConfirmation_token_expiration())
                .is_active(user.getIs_active())
                .is_blacklisted(user.getIs_blacklisted())
                .blacklist_expire_at(user.getBlacklist_expire_at())
                .created_at(user.getCreated_at())
                .updated_at(user.getUpdated_at())
                .build();
    }
}