package darts.ng.io.usersMicroservice.registration_confirmation.repository;

import darts.ng.io.usersMicroservice.registration_confirmation.model.RegisConfirmModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface RegisConfirmRepo extends JpaRepository<RegisConfirmModel,Integer> {
    Optional<RegisConfirmModel> findByEmailAndUserid(String email,  UUID userid);
}
