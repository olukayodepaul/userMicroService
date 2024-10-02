package darts.ng.io.usersMicroservice.utilities;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final Pattern emailPattern;

    public EmailValidator() {
        this.emailPattern = Pattern.compile(EMAIL_REGEX);
    }

    public boolean isValid(String email) {
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
}
