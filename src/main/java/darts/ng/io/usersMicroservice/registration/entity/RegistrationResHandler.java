package darts.ng.io.usersMicroservice.registration.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResHandler {
    private boolean status;
    private String message;
    private Profile user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private UUID userId;
        private String email;
        private String firstName;
        private String lastName;
        private String gender;
        private String dateOfBirth;
        private String token;
        private String profilePictureUrl;
    };

}


