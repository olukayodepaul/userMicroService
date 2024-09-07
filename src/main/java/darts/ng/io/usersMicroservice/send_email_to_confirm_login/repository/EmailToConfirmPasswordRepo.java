package darts.ng.io.usersMicroservice.send_email_to_confirm_login.repository;

import darts.ng.io.usersMicroservice.send_email_to_confirm_login.model.EmailToConfirmPasswordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface EmailToConfirmPasswordRepo extends JpaRepository<EmailToConfirmPasswordModel, Integer> {
    Optional<EmailToConfirmPasswordModel> findByEmail(String Email);
}
