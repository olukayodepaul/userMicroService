package darts.ng.io.usersMicroservice.utilities;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    //limit time
    public static final int EMAIL_CONFIRMATION_EXPIRATION = 180;

    public static final int PASSWORD_EXPIRATION_DURATION = 120;
    public static final String INVALID_UUID = "Token expiration";
    public static final String KAY_ERROR = "Validation error";
    public static final String INVALID_EMAIL = "Invalid email";



    //BRUTE FORCE PROTECTION
    public static final String REGISTRATION_LIMIT = "registration_rate_limit:";
    public static final String SEND_EMAIL_CONFIRM_LIMIT = "send_confirm_email_rate_limit:";
    public static final String CONFIRM_EMAIL_LIMIT = "confirm_email_rate_limit:";
    public static final String LOGIN_RESET_LIMIT = "login_rate_limit:";
    public static final String REQUEST_PASSWORD_RESET_LIMIT = "reset_password_rate_limit:";
    public static final String PASSWORD_RESET_LIMIT = "password_set_rate_limit:";
    public static final String CHANGE_PASSWORD_ON_LOGIN_LIMIT = "send_confirm_email_rate_limit:";
    public static final String GET_BLACK_LIST_LIMIT = "fetch_blacklist_rate_limit:";
    public static final String WHITE_LIST_LIMIT = "whitelist_rate_limit:";

    //////////




    //Message
    public static final String CHANGE_PASSWORD_NOT_MATCH = "New password and confirm password dot not match";


}
