package darts.ng.io.usersMicroservice.darts_app.service.blacklist_service;


import darts.ng.io.usersMicroservice.darts_app.entity.FetchUserDetailsCacheModel;
import darts.ng.io.usersMicroservice.darts_app.entity.WhitelistReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserBlackListedDbModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.mapper.UserRecordMapper;
import darts.ng.io.usersMicroservice.darts_app.kafka.MessageBrokerManager;
import darts.ng.io.usersMicroservice.darts_app.repository.UserBlackListedRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.security.FilterService;
import darts.ng.io.usersMicroservice.utilities.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WhitelistService {

    private static final Logger logger = LoggerFactory.getLogger(WhitelistService.class);
    private final UserDatabaseRepo userDatabaseRepo;
    private final UserRedisCacheRepo cacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final UtilitiesManager utilitiesManager;
    private final FilterService jwtService;
    private final ValidationUtils validationUtils;
    private final DbSaveUpdatedService dbSaveUpdatedService;
    private final UserBlackListedRepo userBlackListedRepo;

    public WhitelistService(
            UserDatabaseRepo userDatabaseRepo,
            UserRedisCacheRepo cacheService,
            MessageBrokerManager messageBrokerManager,
            UtilitiesManager utilitiesManager,
            FilterService jwtService,
            ValidationUtils validationUtils,
            DbSaveUpdatedService dbSaveUpdatedService,
            UserBlackListedRepo userBlackListedRepo
    ) {
        this.userDatabaseRepo = userDatabaseRepo;
        this.cacheService = cacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.utilitiesManager = utilitiesManager;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
        this.dbSaveUpdatedService = dbSaveUpdatedService;
        this.userBlackListedRepo = userBlackListedRepo;
    }

    public ResponseEntity<Void> whitelistUser(WhitelistReqModel request, HttpServletRequest headerRequest, String token) {

        validationUtils.tokenValidateRequest(token);
        validationUtils.reasonValidateRequest(request.getReason());
        cacheService.isTokenBlacklisted(jwtService.extractUUID(token), jwtService.extractTokenFromHeader(token));

        String ipAddress = headerRequest.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = headerRequest.getRemoteAddr();
        }

        UsersDatabaseModel existingUser = userDatabaseRepo.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.KAY_ERROR, AppConfig.INVALID_UUID),
                        HttpStatus.NOT_FOUND
                ));

        validationUtils.bruteForceProtection(existingUser.getEmail(), AppConfig.WHITE_LIST_LIMIT);
        validationUtils.validateAccountNotConfirm(existingUser.getIs_active());
        UsersDatabaseModel updateUser = updateUser(existingUser);
        UserRecordMapper saveResult = dbSaveUpdatedService.saveUpdatedUserRecord(updateUser);

        if (!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "We can't confirm the user at the moment", saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        FetchUserDetailsCacheModel deleteCacheRecord = cacheService.deleteUserDetails(saveResult.getUsers().getEmail());

        //Todo: change this to kafka
        if (deleteCacheRecord.getStatus()) {
            messageBrokerManager.deleteUserDetailsFromCacheMQ("delete", saveResult.getUsers().getEmail());
        }

        UserBlackListedDbModel newBlacklist = blackListTrail(saveResult.getUsers().getUuid(), request.getReason(), ipAddress);
        UserBlackListedDbModel blackListTrailSaveResult = userBlackListedRepo.save(newBlacklist);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Updates the user model with a new confirmation link, token, and expiration time.
     *
     * @param request The user data to be updated.
     * @return UsersDatabaseModel containing updated user details.
     */
    private UsersDatabaseModel updateUser(UsersDatabaseModel request) {
        return UsersDatabaseModel.builder()
                .id(request.getId())
                .uuid(request.getUuid())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .organisation_id(request.getOrganisation_id())
                .password_reset_code(request.getPassword_reset_code())
                .password_reset_expiration(request.getPassword_reset_expiration())
                .confirmation_link(request.getConfirmation_link())
                .confirmation_code(request.getConfirmation_code())
                .confirmation_token_expiration(request.getConfirmation_token_expiration())
                .is_active(request.getIs_active())
                .is_blacklisted(false)
                .blacklist_expire_at(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                .created_at(request.getCreated_at())
                .updated_at(request.getUpdated_at())
                .build();
    }

    private UserBlackListedDbModel blackListTrail(UUID uuid, String reason, String ip) {
        LocalDateTime dateTime = LocalDateTime.now();
        return UserBlackListedDbModel.builder()
                .uuid(uuid)
                .ip_address(ip)
                .reason(reason)
                .is_active(false)
                .created_at(dateTime)
                .updated_at(dateTime)
                .expiry_at(utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00"))
                .build();
    }

}

