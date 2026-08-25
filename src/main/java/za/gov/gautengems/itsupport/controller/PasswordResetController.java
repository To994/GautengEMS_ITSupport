package za.gov.gautengems.itsupport.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import za.gov.gautengems.itsupport.entity.PasswordResetToken;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.EmailService;
import za.gov.gautengems.itsupport.service.PasswordResetService;
import za.gov.gautengems.itsupport.service.UserService;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PasswordResetController(
            PasswordResetService passwordResetService,
            UserService userService,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.passwordResetService = passwordResetService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    // =========================================================
    // FORGOT PASSWORD PAGE
    // =========================================================

    @GetMapping("/forgot-password")
    public String forgotPassword() {

        return "forgot-password";
    }


    // =========================================================
    // PROCESS FORGOT PASSWORD
    // =========================================================

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("usernameOrEmail") String usernameOrEmail,
            Model model) {

        // -----------------------------------------------------
        // REMOVE EXTRA SPACES
        // -----------------------------------------------------

        usernameOrEmail = usernameOrEmail.trim();


        // -----------------------------------------------------
        // FIND USER
        // -----------------------------------------------------

        User user = userService.findByUsername(usernameOrEmail);

        if (user == null) {
            user = userService.findByEmail(usernameOrEmail);
        }


        // -----------------------------------------------------
        // USER NOT FOUND
        // -----------------------------------------------------

        if (user == null) {

            model.addAttribute(
                    "error",
                    "No EMS account was found with that username or email address."
            );

            return "forgot-password";
        }


        // -----------------------------------------------------
        // CHECK USER EMAIL
        // -----------------------------------------------------

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            model.addAttribute(
                    "error",
                    "Your account does not have a valid email address. Please contact IT Support."
            );

            return "forgot-password";
        }


        // -----------------------------------------------------
        // CREATE RESET TOKEN
        // -----------------------------------------------------

        String token =
                passwordResetService.createResetToken(
                        usernameOrEmail
                );


        // -----------------------------------------------------
        // CREATE RESET LINK
        // -----------------------------------------------------

        String resetLink =
                "http://localhost:8080/reset-password?token="
                        + token;


        // -----------------------------------------------------
        // SEND RESET EMAIL
        // -----------------------------------------------------

        try {

            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    resetLink
            );

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "We could not send the password reset email. Please try again later."
            );

            return "forgot-password";
        }


        // -----------------------------------------------------
        // EMAIL SENT SUCCESSFULLY
        // -----------------------------------------------------

        model.addAttribute(
                "success",
                "A password reset link has been sent to your registered email address."
        );

        return "forgot-password";
    }


    // =========================================================
    // RESET PASSWORD PAGE
    // =========================================================

    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam("token") String token,
            Model model) {

        PasswordResetToken resetToken =
                passwordResetService.getValidToken(token);


        // -----------------------------------------------------
        // INVALID / EXPIRED TOKEN
        // -----------------------------------------------------

        if (resetToken == null) {

            model.addAttribute(
                    "error",
                    "This password reset link is invalid or has expired."
            );

            return "reset-password";
        }


        model.addAttribute(
                "token",
                token
        );


        return "reset-password";
    }


    // =========================================================
    // UPDATE PASSWORD
    // =========================================================

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        // -----------------------------------------------------
        // CHECK TOKEN
        // -----------------------------------------------------

        PasswordResetToken resetToken =
                passwordResetService.getValidToken(token);


        if (resetToken == null) {

            model.addAttribute(
                    "error",
                    "This password reset link is invalid or has expired."
            );

            return "reset-password";
        }


        // -----------------------------------------------------
        // CHECK PASSWORD
        // -----------------------------------------------------

        if (password == null
                || password.isBlank()) {

            model.addAttribute(
                    "error",
                    "Please enter a new password."
            );

            model.addAttribute(
                    "token",
                    token
            );

            return "reset-password";
        }


        // -----------------------------------------------------
        // CHECK PASSWORD CONFIRMATION
        // -----------------------------------------------------

        if (!password.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "The passwords do not match."
            );

            model.addAttribute(
                    "token",
                    token
            );

            return "reset-password";
        }


        // -----------------------------------------------------
        // GET USER
        // -----------------------------------------------------

        User user =
                resetToken.getUser();


        // -----------------------------------------------------
        // ENCODE NEW PASSWORD
        // -----------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(password)
        );


        // -----------------------------------------------------
        // SAVE USER
        // -----------------------------------------------------

        userService.saveUserWithoutEncoding(user);


        // -----------------------------------------------------
        // DELETE USED TOKEN
        // -----------------------------------------------------

        passwordResetService.deleteToken(
                resetToken
        );


        // -----------------------------------------------------
        // SUCCESS
        // -----------------------------------------------------

        return "redirect:/login?reset=success";
    }
}