package za.gov.gautengems.itsupport.service;

import org.springframework.stereotype.Service;

import za.gov.gautengems.itsupport.entity.PasswordResetToken;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PasswordResetTokenService(
            PasswordResetTokenRepository tokenRepository) {

        this.tokenRepository = tokenRepository;
    }


    // =========================================================
    // CREATE RESET TOKEN
    // =========================================================

    public PasswordResetToken createToken(User user) {

        /*
         * Remove any existing reset token belonging
         * to this user before creating a new one.
         *
         * This ensures that only the latest reset link
         * remains valid.
         */

        tokenRepository.deleteByUserId(user.getId());


        /*
         * Generate a secure random UUID.
         */

        String token =
                UUID.randomUUID().toString();


        /*
         * Token will expire after 30 minutes.
         */

        LocalDateTime expiryDate =
                LocalDateTime.now()
                        .plusMinutes(30);


        /*
         * Create the reset-token entity.
         */

        PasswordResetToken resetToken =
                new PasswordResetToken(
                        token,
                        user,
                        expiryDate
                );


        /*
         * Save token to MySQL.
         */

        return tokenRepository.save(resetToken);
    }


    // =========================================================
    // FIND TOKEN
    // =========================================================

    public Optional<PasswordResetToken> findByToken(
            String token) {

        return tokenRepository.findByToken(token);
    }


    // =========================================================
    // CHECK TOKEN
    // =========================================================

    public boolean isTokenValid(String token) {

        Optional<PasswordResetToken> resetToken =
                tokenRepository.findByToken(token);


        /*
         * Token does not exist.
         */

        if (resetToken.isEmpty()) {

            return false;
        }


        /*
         * Token exists but has expired.
         */

        return !resetToken.get().isExpired();
    }


    // =========================================================
    // DELETE TOKEN
    // =========================================================

    public void deleteToken(String token) {

        tokenRepository.findByToken(token)
                .ifPresent(tokenRepository::delete);
    }


    // =========================================================
    // DELETE USER TOKEN
    // =========================================================

    public void deleteUserToken(Long userId) {

        tokenRepository.deleteByUserId(userId);
    }
}