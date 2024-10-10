package darts.ng.io.usersMicroservice.darts_app.grpc.client;


import darts.grpc.client.UserProfileGrpc;
import darts.grpc.client.UserProfileOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.springframework.stereotype.Service;

@Service
public class GrpcUserProfileClientImpl {

    private final ManagedChannel managedChannel;

    public GrpcUserProfileClientImpl(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    public Boolean addProfile(
            String jwtToken,
            String uuid,
            String firstName,
            String lastName,
            String phoneNumber,
            String dateOfBirth,
            String gender,
            String bio,
            String organisationId) {

        Metadata metadata = new Metadata();

        Metadata.Key<String> AUTHORIZATION_HEADER =
                Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(AUTHORIZATION_HEADER, "Bearer " + jwtToken);

        //todo: change the deprecated attachHeaders to t=latest one....
        UserProfileGrpc.UserProfileBlockingStub stub = UserProfileGrpc.newBlockingStub(managedChannel);
        stub = MetadataUtils.attachHeaders(stub, metadata);

        UserProfileOuterClass.Profile profile = UserProfileOuterClass.Profile.newBuilder()
                .setUuid(uuid)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhoneNumber(phoneNumber)
                .setDateOfBirth(dateOfBirth)
                .setGender(gender)
                .setBio(bio)
                .setOrganisationId(organisationId)
                .build();
        try {
            UserProfileOuterClass.Response result =  stub.addProfile(profile);
            return result.getStatus();
        } catch (Exception e) {
            return false;
        }
    }
}
