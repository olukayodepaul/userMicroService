package darts.ng.io.usersMicroservice.darts_app.service.user_account_service;

import darts.ng.io.usersMicroservice.darts_app.entity.UserRegistrationResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.UserRegistrationReqModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.mapper.UserRecordMapper;
import darts.ng.io.usersMicroservice.darts_app.grpc.client.GrpcUserProfileClientImpl;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.security.FilterService;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserRegistrationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationServiceImpl.class);

    private final UserDatabaseRepo registrationRepo;
    private final UtilitiesManager utilitiesManager;
    private final ValidationUtils validationUtils;
    private final DbSaveUpdatedService dbSaveUpdatedService;
    private final FilterService jwtService;
    private final GrpcUserProfileClientImpl grpcUserProfileClient;

    /**
     * Constructor for UserRegistrationServiceImpl.
     *
     * @param registrationRepo      Repository for user data operations.
     * @param utilitiesManager      Utility class for general functions like UUID generation.
     * @param validationUtils       Utility class for validating input data like email and password.
     * @param dbSaveUpdatedService  Service for saving or updating user data in the database.
     */
    public UserRegistrationServiceImpl(
            UserDatabaseRepo registrationRepo,
            UtilitiesManager utilitiesManager,
            ValidationUtils validationUtils,
            DbSaveUpdatedService dbSaveUpdatedService,
            FilterService jwtService,
            GrpcUserProfileClientImpl grpcUserProfileClient
    ) {
        this.registrationRepo = registrationRepo;
        this.utilitiesManager = utilitiesManager;
        this.validationUtils = validationUtils;
        this.dbSaveUpdatedService = dbSaveUpdatedService;
        this.jwtService = jwtService;
        this.grpcUserProfileClient = grpcUserProfileClient;
    }

    /**
     * Registers a new user in the system.
     *
     * @param request The user registration request containing user details.
     * @return ResponseEntity containing the registration response model.
     * @throws CustomRuntimeException if validation fails or user already exists.
     */
    public ResponseEntity<UserRegistrationResModel> userRegistration(UserRegistrationReqModel request) {

        validationUtils.bruteForceProtection(request.getEmail(), AppConfig.REGISTRATION_LIMIT);

        // Validate the incoming request and email
        validationUtils.userValidateRequest(request);
        validationUtils.sanitizeEmail(request.getEmail());

        // Check if the email is already registered
        Optional<UsersDatabaseModel> dbExistingUser = registrationRepo.findByEmail(request.getEmail());

        if (dbExistingUser.isPresent()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "validation check", "Email is already taken"),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Create a new user model based on the request data
        UsersDatabaseModel newUser = createUser(request);

        // Save the user record in the database
        UserRecordMapper saveResult = dbSaveUpdatedService.saveUpdatedUserRecord(newUser);

        // If saving the user fails, throw a custom runtime exception
        if (!saveResult.getStatus()) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "registration error", saveResult.getError()),
                    HttpStatus.BAD_REQUEST
            );
        }

        UsersDatabaseModel profile = saveResult.getUsers();

        String requestToken = jwtService.jwtToken(profile.getUuid().toString(), profile.getEmail(), request.getOrganisation_id().toString());

       //todo: get the grpc response, if not successful, then call the kafka service.
        if(!grpcUserProfileClient.addProfile(
                requestToken,
                profile.getUuid().toString(),
                request.getDetails().getFirst_name(),
                request.getDetails().getLast_name(),
                request.getDetails().getPhone_number(),
                request.getDetails().getDate_of_birth(),
                request.getDetails().getGender(),
                request.getDetails().getBio(),
                request.getOrganisation_id().toString())
        ) {
           System.out.println("here is false");
        }

        // Return the response with a 201 status code (CREATED) and user details
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(request, newUser));
    }

    /**
     * Creates a new user model for database storage.
     *
     * @param request The user registration request containing user details.
     * @return UsersDatabaseModel containing the new user information.
     */
    private UsersDatabaseModel createUser(UserRegistrationReqModel request) {
        UUID emailToUUID = utilitiesManager.generateUUID(request.getEmail());
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime initialDate = utilitiesManager.convertStringToDateTime("1900-01-01 00:00:00");
        return UsersDatabaseModel.builder()
                .id(0)
                .uuid(emailToUUID)
                .email(request.getEmail())
                .password(validationUtils.plainTextEncryption(request.getPassword()))
                .role(request.getRole())
                .organisation_id(request.getOrganisation_id())
                .password_reset_code("")
                .password_reset_expiration(initialDate)
                .confirmation_link("")
                .confirmation_code("")
                .confirmation_token_expiration(initialDate)
                .is_active(false)
                .is_blacklisted(false)
                .blacklist_expire_at(initialDate)
                .created_at(dateTime)
                .updated_at(dateTime)
                .build();
    }

    /**
     * Builds the response model to be returned after successful registration.
     *
     * @param request The user registration request.
     * @param newUser The newly created user.
     * @return UserRegistrationResModel containing success message and user details.
     */
    private UserRegistrationResModel buildResponse(UserRegistrationReqModel request, UsersDatabaseModel newUser) {
        return new UserRegistrationResModel(
                true,
                "Account successfully created",
                new UserRegistrationResModel.Details(
                        request.getEmail(),
                        request.getRole(),
                        request.getDetails().getFirst_name(),
                        request.getDetails().getLast_name(),
                        request.getDetails().getPhone_number(),
                        request.getDetails().getDate_of_birth(),
                        request.getDetails().getGender(),
                        request.getDetails().getBio(),
                        request.getOrganisation_id(),
                        false,
                        false,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }
}
