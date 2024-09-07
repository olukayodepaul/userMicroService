package darts.ng.io.usersMicroservice.change_login_password.repository;


import darts.ng.io.usersMicroservice.change_login_password.model.ChangePasswordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChangePasswordRepo extends JpaRepository<ChangePasswordModel, Integer> {
    Optional<ChangePasswordModel> findByEmail(String Email);
}
