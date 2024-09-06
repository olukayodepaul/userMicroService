package darts.ng.io.usersMicroservice.registration.data;


import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegRequest {
    private User user;
    private Profile profile;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String email;
        private String passwordHash;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;
        private String dateOfBirth;
        private String bio;
        private String profilePictureUrl;
    }
}
