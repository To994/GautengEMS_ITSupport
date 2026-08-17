package za.gov.gautengems.itsupport.repository;

import za.gov.gautengems.itsupport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPersonalNumber(String personalNumber);
}