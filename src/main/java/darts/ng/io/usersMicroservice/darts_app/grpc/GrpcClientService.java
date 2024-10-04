package darts.ng.io.usersMicroservice.darts_app.grpc;

import com.grpc.darts.DetailsOuterClass;
import com.grpc.darts.DetailsServiceGrpc;
import org.springframework.stereotype.Service;
import io.grpc.ManagedChannel;

import io.grpc.stub.StreamObserver;
import io.grpc.StatusRuntimeException;

@Service
public class GrpcClientService {

    private final DetailsServiceGrpc.DetailsServiceStub detailsServiceStub;

    public GrpcClientService(ManagedChannel managedChannel) {
        this.detailsServiceStub = DetailsServiceGrpc.newStub(managedChannel);

    }

    public void grpcAddDetailsAsync(String uuid, String firstName, String lastName, String phoneNumber, String dob, String gender, String bio) {
        DetailsOuterClass.Details request = DetailsOuterClass.Details.newBuilder()
                .setUuid(uuid)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhoneNumber(phoneNumber)
                .setDateOfBirth(dob)
                .setGender(gender)
                .setBio(bio)
                .build();


        detailsServiceStub.addDetails(request, new StreamObserver<>() {
            @Override
            public void onNext(DetailsOuterClass.AddDetailsResponse response) {
                // Successful response handling
                System.out.println("Response: " + response);
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof StatusRuntimeException) {
                    StatusRuntimeException statusException = (StatusRuntimeException) throwable;
                    System.out.println("gRPC call failed with status: " + statusException.getStatus().getCode());
                    System.out.println("Status description: " + statusException.getStatus().getDescription());
                } else {
                    // Handle other types of errors
                    System.out.println("An unexpected error occurred: " + throwable.getMessage());
                }
            }

            @Override
            public void onCompleted() {
                // Stream completed
            }
        });


    }


}