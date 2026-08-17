package za.gov.gautengems.itsupport.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // SAVE USER
    // =========================================================

    public User saveUser(User user) {

        /*
         * Only encode a password if it is not already BCrypt encoded.
         * This prevents an already encrypted password from being
         * encrypted again.
         */
        if (user.getPassword() != null
                && !user.getPassword().startsWith("$2a$")
                && !user.getPassword().startsWith("$2b$")
                && !user.getPassword().startsWith("$2y$")) {

            user.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        return userRepository.save(user);
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // =========================================================
    // FIND BY USERNAME
    // =========================================================

    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }


    // =========================================================
    // FIND BY EMAIL
    // =========================================================

    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }


    // =========================================================
    // FIND BY PERSONAL NUMBER
    // =========================================================

    public Optional<User> findByPersonalNumber(String personalNumber) {

        return userRepository.findByPersonalNumber(personalNumber);
    }


    // =========================================================
    // DELETE USER
    // =========================================================

    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }


    // =========================================================
    // GET ACTIVE TECHNICIANS
    // =========================================================

    public List<User> getActiveTechnicians() {

        return userRepository.findAll()
                .stream()
                .filter(user ->
                        user.getRole() == User.Role.TECHNICIAN
                                && user.isActive()
                )
                .toList();
    }
}