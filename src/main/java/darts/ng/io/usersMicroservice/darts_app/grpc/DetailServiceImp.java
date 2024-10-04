package darts.ng.io.usersMicroservice.darts_app.grpc;

import com.grpc.darts.DetailsOuterClass;
import com.grpc.darts.DetailsServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class DetailServiceImp extends DetailsServiceGrpc.DetailsServiceImplBase {

    private final UserDetailsService userDetailsService;

    public DetailServiceImp() {
        this.userDetailsService = new UserDetailsService();
    }

    @Override
    public void addDetails(DetailsOuterClass.Details request, StreamObserver<DetailsOuterClass.AddDetailsResponse> responseObserver) {
        // Extract data from request
        String uuid = request.getUuid();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String phoneNumber = request.getPhoneNumber();
        String dob = request.getDateOfBirth();
        String gender = request.getGender();
        String bio = request.getBio();

        DetailsOuterClass.Details detailsToSave = DetailsOuterClass.Details.newBuilder()
                .setUuid(uuid)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhoneNumber(phoneNumber)
                .setDateOfBirth(dob)
                .setGender(gender)
                .setBio(bio)
                .build();

        userDetailsService.saveDetails(detailsToSave);

        // Handle business logic (e.g., store details in a database)

        // Prepare response
        DetailsOuterClass.AddDetailsResponse response = DetailsOuterClass.AddDetailsResponse.newBuilder()
                .setStatus("success")
                .setMessage("Details added successfully")
                .build();

        // Send response to client
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
