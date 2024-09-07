package darts.ng.io.usersMicroservice.util;


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
public class UUIDManager {

    public UUID generateUUID(String email) {
        UUID namespace = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
        String currentDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"));
        String combined = email + currentDateTime;
        return UUID.nameUUIDFromBytes((namespace.toString() + combined).getBytes(StandardCharsets.UTF_8));
    }

    public boolean isValidUUID(String uuidString) {
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public LocalDateTime expiryDate(int day) {
        return LocalDateTime.now().plus(day, ChronoUnit.DAYS);
    }

    public LocalDateTime expiryTime(int minute) {
        return LocalDateTime.now().plus(minute, ChronoUnit.MINUTES);
    }

    public Integer SixRandomDigitNumberGenerator() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    public  String generateVerificationString() {
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

}
