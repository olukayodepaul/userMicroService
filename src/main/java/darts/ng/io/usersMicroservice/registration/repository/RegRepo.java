package darts.ng.io.usersMicroservice.registration.repository;

import darts.ng.io.usersMicroservice.registration.model.RegModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RegRepo extends JpaRepository<RegModel, Integer> {
    boolean existsByEmail(String email);
}