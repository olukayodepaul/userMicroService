package darts.ng.io.usersMicroservice.registration.entity;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegRequestHandler {
    private User user;
    private Profile profile;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String email;
        private String passwordHash;
        private String username; // Added username field
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String dateOfBirth;
        private String gender; // Added gender field
        private String bio;
        private String profilePictureUrl;
        private List<Address> addresses; // Added addresses field
//        private List<PaymentMethod> paymentMethods; // Added payment methods field
//        private Preferences preferences; // Added preferences field
//        private String membershipStatus; // Added membership status field

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Address {
            private String type; // e.g., home, office
            private String addressLine1;
            private String addressLine2;
            private String city;
            private String state;
            private String postalCode;
            private String country;
        }
    }

//    //Individual organisation using this media can provide their security mechanism
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class PaymentMethod {
//        private String cardType; // e.g., Visa, MasterCard
//        private String cardNumber; // Should be masked or encrypted in real scenarios
//        private String expiryDate;
//        private String billingAddress; // Consider using Address class for better structure
//    }
//
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class Preferences {
//        private boolean newsletterSubscribed;
//        private String theme; // e.g., dark, light
//    }
}
