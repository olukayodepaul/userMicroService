package darts.ng.io.usersMicroservice.user_registration_email_confirm.repository;

import darts.ng.io.usersMicroservice.user_registration_email_confirm.data.ConfirmRegModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ConfirmRegRepo extends JpaRepository<ConfirmRegModel, Integer> {
    ConfirmRegModel findByEmailAndUserid(String email, UUID userid);
}
