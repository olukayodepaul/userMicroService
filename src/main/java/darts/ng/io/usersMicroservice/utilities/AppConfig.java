package darts.ng.io.usersMicroservice.utilities;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    public static final int EMAIL_CONFIRMATION_EXPIRATION = 120;
    public static final int PASSWORD_EXPIRATION = 120;
    public static final String CHANGE_PASSWORD_NOT_MATCH = "New password and confirm password dot not match";
    public static final String INVALID_UUID = "Token expiration";
    public static final String KAY_ERROR = "Validation error";

    public static final String INVALID_EMAIL = "Invalid email";


    //BRUTE FORCE PROTECTION
    public static final String REGISTRATION_LIMIT = "registration_rate_limit:";

    //////////


    public static final String REQUEST_PASSWORD_RESET_LIMIT = "reset_password_rate_limit:";
    public static final String LOGIN_RESET_LIMIT = "login_rate_limit:";
    public static final String WHITE_LIST_LIMIT = "whitelist_rate_limit:";


}
