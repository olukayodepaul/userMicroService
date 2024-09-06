package darts.ng.io.usersMicroservice.registration.data;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseHandler {
    private boolean status;
    private String message;
    private UserProfiles userProfile;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfiles {
        private Users user;
        private UserProfile profile;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Users {
            private String email;
            private String token;
            private UUID userId;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UserProfile {
            private String firstName;
            private String lastName;
            private String phoneNumber;
            private String address;
            private String dateOfBirth;
            private String bio;
            private String profilePictureUrl;
        }
    }
}
