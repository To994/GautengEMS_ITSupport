package za.gov.gautengems.itsupport.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with ID: " + id
                        )
                );
    }


    // =========================================================
    // SAVE NEW USER
    // =========================================================

    public User saveUser(User user) {

        // Encode the password before saving it to MySQL.
        // NEVER store a plain-text password.

        if (user.getPassword() != null
                && !user.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );
        }

        return userRepository.save(user);
    }


    // =========================================================
    // UPDATE USER
    // =========================================================

    public User updateUser(Long id, User updatedUser) {

        User existingUser = getUserById(id);


        // -----------------------------------------------------
        // PERSONAL INFORMATION
        // -----------------------------------------------------

        existingUser.setPersonalNumber(
                updatedUser.getPersonalNumber()
        );

        existingUser.setFirstName(
                updatedUser.getFirstName()
        );

        existingUser.setSurname(
                updatedUser.getSurname()
        );

        existingUser.setEmail(
                updatedUser.getEmail()
        );

        existingUser.setPhone(
                updatedUser.getPhone()
        );


        // -----------------------------------------------------
        // EMS INFORMATION
        // -----------------------------------------------------

        existingUser.setDepartment(
                updatedUser.getDepartment()
        );

        existingUser.setDistrict(
                updatedUser.getDistrict()
        );

        existingUser.setStationUnit(
                updatedUser.getStationUnit()
        );


        // -----------------------------------------------------
        // ACCOUNT INFORMATION
        // -----------------------------------------------------

        existingUser.setUsername(
                updatedUser.getUsername()
        );

        existingUser.setRole(
                updatedUser.getRole()
        );

        existingUser.setActive(
                updatedUser.isActive()
        );


        // -----------------------------------------------------
        // PASSWORD
        // -----------------------------------------------------

        /*
         * Only change the password if the administrator
         * entered a new password.
         *
         * The new password is BCrypt encoded before
         * being stored in MySQL.
         */

        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(
                            updatedUser.getPassword()
                    )
            );
        }


        // -----------------------------------------------------
        // SAVE UPDATED USER
        // -----------------------------------------------------

        return userRepository.save(existingUser);
    }


    // =========================================================
    // DELETE USER
    // =========================================================

    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }


    // =========================================================
    // FIND USER BY USERNAME
    // =========================================================

    public User findByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElse(null);
    }


    // =========================================================
    // FIND USER BY EMAIL
    // =========================================================

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElse(null);
    }

    // =========================================================
    // SAVE USER WITHOUT RE-ENCODING PASSWORD
    // =========================================================

    public User saveUserWithoutEncoding(User user) {

        return userRepository.save(user);
    }

}