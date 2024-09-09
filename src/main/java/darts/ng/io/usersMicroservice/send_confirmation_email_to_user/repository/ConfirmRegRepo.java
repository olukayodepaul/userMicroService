package darts.ng.io.usersMicroservice.send_confirmation_email_to_user.repository;

import darts.ng.io.usersMicroservice.send_confirmation_email_to_user.entity.ConfirmRegModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ConfirmRegRepo extends JpaRepository<ConfirmRegModel, Integer> {
    ConfirmRegModel findByEmailAndUserid(String email, UUID userid);
}
