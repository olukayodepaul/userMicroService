package darts.ng.io.usersMicroservice.registration_confirmation.service;

import darts.ng.io.usersMicroservice.registration_confirmation.model.RegisConfirmModel;
import darts.ng.io.usersMicroservice.registration_confirmation.model.RegisConfirmReq;
import darts.ng.io.usersMicroservice.registration_confirmation.repository.RegisConfirmRepo;
import darts.ng.io.usersMicroservice.util.CustomException;
import darts.ng.io.usersMicroservice.util.EmailValidator;
import darts.ng.io.usersMicroservice.util.RegErrorHandler;
import darts.ng.io.usersMicroservice.util.UUIDManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegisConfirmImpl {

    private final RegisConfirmRepo regisConfirmRepo;
    private final EmailValidator emailValidator;
    private final UUIDManager uuidManager;

    public RegisConfirmImpl(RegisConfirmRepo registration, EmailValidator emailValidator, UUIDManager uuidManager) {
        this.regisConfirmRepo = registration;
        this.emailValidator = emailValidator;
        this.uuidManager = uuidManager;
    }

    public ResponseEntity<?> RegConfirm(RegisConfirmReq request) {

        // Check if the request is null or has invalid data
        if (request == null || request.getEmail() == null || request.getUserId() == null || request.getAccessCode() == null) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid input. Please ensure all required fields are provided."),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Validate email format
        if (!emailValidator.isValid(request.getEmail())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid email format. Please provide a valid email address."),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Validate user ID as UUID
        if (!uuidManager.isValidUUID(request.getUserId())) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid user ID. Please provide a valid user identifier."),
                    HttpStatus.BAD_REQUEST
            );
        }

        UUID userid = UUID.fromString(request.getUserId());
        Optional<RegisConfirmModel> response = regisConfirmRepo.findByEmailAndUserid(request.getEmail(), userid);

        if (response.isPresent()) {

            RegisConfirmModel confirmModel = response.get();

            if (confirmModel.getConfirmcode() == null || confirmModel.getConfirmtoken() == null) {
                return new ResponseEntity<>(
                        new RegErrorHandler(false, "Access code or user id not found. Please check your input and try again"),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (!confirmModel.getConfirmcode().equalsIgnoreCase(request.getAccessCode()) &&
                    !confirmModel.getConfirmtoken().equalsIgnoreCase(request.getAccessCode())) {
                return new ResponseEntity<>(
                        new RegErrorHandler(false, "Invalid confirmation code or user ID. Please try again."),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (response.get().getConfirmtokenexpire().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(
                        new RegErrorHandler(false, "Your confirmation code has expired. Please request a new one."),
                        HttpStatus.GONE
                );
            }

            response.get().setConfirmtokenexpire(null);
            response.get().setConfirmcode(null);
            response.get().setConfirmtoken(null);
            response.get().setStatus(true);
            regisConfirmRepo.save(response.get());

            return new ResponseEntity<>(
                    new RegErrorHandler(true, "Email successfully confirmed. Enjoy our services!"),
                    HttpStatus.OK
            );

        } else {
            return new ResponseEntity<>(
                    new RegErrorHandler(false, "User not found."),
                    HttpStatus.NOT_FOUND
            );
        }
    }
}
