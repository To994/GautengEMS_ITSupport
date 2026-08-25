package za.gov.gautengems.itsupport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import za.gov.gautengems.itsupport.entity.PasswordResetToken;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {


    // =========================================================
    // FIND TOKEN
    // =========================================================

    Optional<PasswordResetToken> findByToken(String token);


    // =========================================================
    // FIND TOKEN BY USER
    // =========================================================

    Optional<PasswordResetToken> findByUserId(Long userId);


    // =========================================================
    // DELETE TOKEN BY USER
    // =========================================================

    void deleteByUserId(Long userId);


    // =========================================================
    // CHECK IF TOKEN EXISTS
    // =========================================================

    boolean existsByToken(String token);
}