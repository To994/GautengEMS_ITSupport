package za.gov.gautengems.itsupport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.gov.gautengems.itsupport.entity.PasswordResetToken;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.repository.PasswordResetTokenRepository;
import za.gov.gautengems.itsupport.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }


    // =========================================================
    // CREATE PASSWORD RESET TOKEN
    // =========================================================

    @Transactional
    public String createResetToken(String usernameOrEmail) {

        // -----------------------------------------------------
        // FIND USER BY USERNAME
        // -----------------------------------------------------

        User user = userRepository
                .findByUsername(usernameOrEmail)
                .orElse(null);


        // -----------------------------------------------------
        // IF USERNAME WAS NOT FOUND, TRY EMAIL
        // -----------------------------------------------------

        if (user == null) {

            user = userRepository
                    .findByEmail(usernameOrEmail)
                    .orElse(null);
        }


        // -----------------------------------------------------
        // USER DOES NOT EXIST
        // -----------------------------------------------------

        if (user == null) {

            return null;
        }


        // -----------------------------------------------------
        // GENERATE NEW SECURE TOKEN
        // -----------------------------------------------------

        String token =
                UUID.randomUUID().toString();


        // -----------------------------------------------------
        // TOKEN EXPIRES AFTER 30 MINUTES
        // -----------------------------------------------------

        LocalDateTime expiryDate =
                LocalDateTime.now()
                        .plusMinutes(30);


        // -----------------------------------------------------
        // CHECK IF USER ALREADY HAS A RESET TOKEN
        // -----------------------------------------------------

        Optional<PasswordResetToken> existingToken =
                tokenRepository.findByUserId(user.getId());


        PasswordResetToken resetToken;


        // -----------------------------------------------------
        // UPDATE EXISTING TOKEN
        // -----------------------------------------------------

        if (existingToken.isPresent()) {

            resetToken = existingToken.get();

            resetToken.setToken(token);

            resetToken.setExpiryDate(expiryDate);

        }


        // -----------------------------------------------------
        // CREATE NEW TOKEN
        // -----------------------------------------------------

        else {

            resetToken =
                    new PasswordResetToken(
                            token,
                            user,
                            expiryDate
                    );
        }


        // -----------------------------------------------------
        // SAVE TOKEN
        // -----------------------------------------------------

        tokenRepository.save(resetToken);


        // -----------------------------------------------------
        // RETURN TOKEN
        // -----------------------------------------------------

        return token;
    }


    // =========================================================
    // FIND VALID TOKEN
    // =========================================================

    @Transactional
    public PasswordResetToken getValidToken(String token) {

        PasswordResetToken resetToken =
                tokenRepository
                        .findByToken(token)
                        .orElse(null);


        // -----------------------------------------------------
        // TOKEN DOES NOT EXIST
        // -----------------------------------------------------

        if (resetToken == null) {

            return null;
        }


        // -----------------------------------------------------
        // TOKEN HAS EXPIRED
        // -----------------------------------------------------

        if (resetToken.isExpired()) {

            tokenRepository.delete(resetToken);

            return null;
        }


        // -----------------------------------------------------
        // TOKEN IS VALID
        // -----------------------------------------------------

        return resetToken;
    }


    // =========================================================
    // DELETE TOKEN
    // =========================================================

    @Transactional
    public void deleteToken(PasswordResetToken token) {

        tokenRepository.delete(token);
    }
}