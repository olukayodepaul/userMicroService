package darts.ng.io.usersMicroservice.util;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class UUIDGenerator {
    public UUID generateUUID(String email) {
        UUID namespace = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"));
        String combined = email + currentDateTime;
        return UUID.nameUUIDFromBytes((namespace.toString() + combined).getBytes(StandardCharsets.UTF_8));
    }
}
