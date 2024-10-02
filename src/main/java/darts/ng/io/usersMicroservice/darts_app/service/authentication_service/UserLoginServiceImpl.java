package darts.ng.io.usersMicroservice.darts_app.service.authentication_service;


import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.security.JwtService;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
public class UserLoginServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);
    private final UserDatabaseRepo registrationRepo;
    private final EmailValidator emailValidator;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final BCryptPasswordEncoder encoder;
    private final UtilitiesManager utilitiesManager;
    private final JwtService jwtService;
    private final ValidationUtils validationUtils;
    private final DatabaseSaveUpdatedService databaseSaveUpdatedService;

    public UserLoginServiceImpl(
            UserDatabaseRepo registrationRepo,
            EmailValidator emailValidator,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager,
            UtilitiesManager utilitiesManager,
            JwtService jwtService,
            ValidationUtils validationUtils,
            DatabaseSaveUpdatedService databaseSaveUpdatedService
    ) {
        this.registrationRepo = registrationRepo;
        this.emailValidator = emailValidator;
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.encoder = new BCryptPasswordEncoder(12);
        this.utilitiesManager = utilitiesManager;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
        this.databaseSaveUpdatedService = databaseSaveUpdatedService;
    }

    public ResponseEntity<UserLoginResModel> userAuthentication(UserLoginReqModel bodyRequest) {

        validationUtils.passwordValidateRequest(bodyRequest);
        validationUtils.validateEmail(bodyRequest.getEmail());

        FetchUserDetailsCacheModel userFromCacheByEmail = cacheService.fetchUserDetails(bodyRequest.getEmail());

        if (userFromCacheByEmail.getStatus()) {

            UserCacheModel existingUserInCache = userFromCacheByEmail.getUserDetails();

//            validationUtils.validateAccountStatus(existingUserInCache.getIs_active(), existingUserInCache.getIs_blacklisted());
//            validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), utilitiesManager.convertStringToDateTime(existingUserInCache.getBlacklist_expire_at()));

            if (!encoder.matches(bodyRequest.getPassword(), existingUserInCache.getPassword())) {
                throw new CustomRuntimeException(
                        new ErrorHandler(false,
                                "Password not match",
                                "Invalid password. Please try again."),
                        HttpStatus.UNAUTHORIZED
                );
            }

            String jwtToken = jwtToken(existingUserInCache.getId().toString(), existingUserInCache.getEmail(), existingUserInCache.getOrganisation_id().toString());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new UserLoginResModel(
                            true,
                            "New user successfully created",
                            new UserLoginResModel.Details(
                                    existingUserInCache.getId(),
                                    existingUserInCache.getUser_id(),
                                    existingUserInCache.getEmail(),
                                    existingUserInCache.getRole(),
                                    existingUserInCache.getOrganisation_id(),
                                    existingUserInCache.getIs_active(),
                                    existingUserInCache.getIs_blacklisted(),
                                    utilitiesManager.convertStringToDateTime(existingUserInCache.getCreated_at()),
                                    utilitiesManager.convertStringToDateTime(existingUserInCache.getUpdated_at()),
                                    jwtToken
                            )
                    ));

        }

        Optional<UsersDatabaseModel> userFromDbByEmail = registrationRepo.findByEmail(bodyRequest.getEmail());

        if (userFromDbByEmail.isPresent()) {

            UsersDatabaseModel existingUserInDb = userFromDbByEmail.get();

//            validationUtils.validateAccountStatus(existingUserInDb.getIs_active(), existingUserInDb.getIs_blacklisted());
//            validationUtils.validateBlackListExpirationDate(LocalDateTime.now(), existingUserInDb.getBlacklist_expire_at());

            if (!encoder.matches(bodyRequest.getPassword(), existingUserInDb.getPassword())) {
                throw new CustomRuntimeException(
                        new ErrorHandler(false,
                                "Invalid reset code",
                                "Kindly provide valid reset code"),
                        HttpStatus.UNAUTHORIZED
                );
            }

            UserCacheModel updatedCacheModel = UserCacheModel.builder()
                    .id(existingUserInDb.getId())
                    .user_id(existingUserInDb.getUser_id())
                    .email(existingUserInDb.getEmail())
                    .password(existingUserInDb.getPassword())
                    .role(existingUserInDb.getRole())
                    .organisation_id(existingUserInDb.getOrganisation_id())
                    .password_reset_code(existingUserInDb.getPassword_reset_code())
                    .password_reset_expiration(utilitiesManager.convertDateTimeToString(existingUserInDb.getPassword_reset_expiration()))
                    .confirmation_link(existingUserInDb.getConfirmation_link())
                    .confirmation_code(existingUserInDb.getConfirmation_code())
                    .confirmation_token_expiration(utilitiesManager.convertDateTimeToString(existingUserInDb.getConfirmation_token_expiration()))
                    .is_active(existingUserInDb.getIs_active())
                    .is_blacklisted(existingUserInDb.getIs_blacklisted())
                    .blacklist_expire_at(utilitiesManager.convertDateTimeToString(existingUserInDb.getBlacklist_expire_at()))
                    .created_at(utilitiesManager.convertDateTimeToString(existingUserInDb.getCreated_at()))
                    .updated_at(utilitiesManager.convertDateTimeToString(existingUserInDb.getUpdated_at()))
                    .build();

            Boolean saveIntoRedisCache = cacheService.saveUpdateUserDetails(updatedCacheModel);

            //todo: if cache failed, then push to messageBroker(Kafka) for retry
            if (!saveIntoRedisCache) {
                messageBrokerManager.updateUserDetailsThroughMQ("update", updatedCacheModel);
            }

            String jwtToken = jwtToken(existingUserInDb.getId().toString(), existingUserInDb.getEmail(), existingUserInDb.getOrganisation_id().toString());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new UserLoginResModel(
                            true,
                            "New user successfully created",
                            new UserLoginResModel.Details(
                                    existingUserInDb.getId(),
                                    existingUserInDb.getUser_id(),
                                    existingUserInDb.getEmail(),
                                    existingUserInDb.getRole(),
                                    existingUserInDb.getOrganisation_id(),
                                    existingUserInDb.getIs_active(),
                                    existingUserInDb.getIs_blacklisted(),
                                    existingUserInDb.getCreated_at(),
                                    existingUserInDb.getUpdated_at(),
                                    jwtToken
                            )
                    ));
        }

        throw new CustomRuntimeException(
                new ErrorHandler(false,
                        "Invalid login credential",
                        "kindly provide valid login credential"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private String jwtToken(String Id, String email, String organisationId) {
        return jwtService.generateToken(new HashMap<>(), Id, email, organisationId);
    }

}
