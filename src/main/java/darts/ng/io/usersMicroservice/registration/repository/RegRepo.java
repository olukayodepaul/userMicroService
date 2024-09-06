package darts.ng.io.usersMicroservice.registration.repository;

import darts.ng.io.usersMicroservice.registration.data.RegistrationDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RegRepo extends JpaRepository<RegistrationDao, Integer> {
    boolean existsByEmail(String email);
}