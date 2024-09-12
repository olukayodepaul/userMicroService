package darts.ng.io.usersMicroservice.util;

public class AuthHeaderUtils {

    /**
     * Cleans the Authorization header value by removing duplicate "Bearer" prefixes
     * and trimming any extra spaces.
     *
     * @param authHeader The raw Authorization header value.
     * @return A cleaned Authorization header value.
     */
    public static String cleanAuthorizationHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }

        // Trim any leading or trailing spaces
        String trimmedHeader = authHeader.trim();

        // Remove any leading "Bearer " prefixes
        String cleanedHeader = trimmedHeader.replaceFirst("^Bearer\\s+", "");

        // Remove any additional "Bearer " prefixes (if there were more than one)
        cleanedHeader = cleanedHeader.replaceFirst("^Bearer\\s+", "");

        // Return cleaned header value
        return "Bearer " + cleanedHeader;
    }
}
