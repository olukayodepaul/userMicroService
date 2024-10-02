package darts.ng.io.usersMicroservice.utilities;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.security.MessageDigest;

@Component
public class UtilitiesManager {


    public String generateVerificationString() {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 168) {
            String randomUUID = UUID.randomUUID().toString();
            sb.append(hashAndEncode(randomUUID));
        }
        String verificationString = sb.substring(0, 168);
        return formatWithHyphens(verificationString);
    }

    public String hashAndEncode(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash).replaceAll("=", "");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating verification string", e);
        }
    }

    private static String formatWithHyphens(String input) {
        return input.replaceAll("(.{12})(?!$)", "$1-");
    }

    public Integer SixRandomDigitNumberGenerator() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    public LocalDateTime expiryDatePeriod(Integer times) {
        return LocalDateTime.now().plus(times, ChronoUnit.SECONDS);
    }

    public LocalDateTime convertStringToDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public String convertDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public String generateUUID(String email) {
        UUID namespace = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
        String currentDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"));
        String combined = email + currentDateTime;
        return UUID.nameUUIDFromBytes((namespace.toString() + combined).getBytes(StandardCharsets.UTF_8)).toString();
    }

    public boolean isValidNumber(String period_in_second) {
        try {
            Long.parseLong(period_in_second);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Integer convertStringToNumber(String period_in_second) {
        return  Integer.parseInt(period_in_second);
    }




}
