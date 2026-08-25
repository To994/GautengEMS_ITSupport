package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.UserService;

@Controller
public class SettingsController {

    private final UserService userService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SettingsController(UserService userService) {
        this.userService = userService;
    }


    // =========================================================
    // SETTINGS PAGE
    // =========================================================

    @GetMapping("/settings")
    public String settings(
            Authentication authentication,
            Model model) {

        // =====================================================
        // SECURITY CHECK
        // =====================================================

        if (authentication == null) {
            return "redirect:/login?error=true";
        }


        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );


        if (!isAdmin) {
            return "redirect:/login?error=true";
        }


        // =====================================================
        // GET LOGGED-IN ADMIN
        // =====================================================

        User currentUser =
                userService.findByUsername(
                        authentication.getName()
                );


        if (currentUser == null) {
            return "redirect:/login?error=true";
        }


        // =====================================================
        // ACCOUNT INFORMATION
        // =====================================================

        model.addAttribute(
                "user",
                currentUser
        );


        model.addAttribute(
                "username",
                authentication.getName()
        );


        // =====================================================
        // SYSTEM INFORMATION
        // =====================================================

        model.addAttribute(
                "systemName",
                "Gauteng EMS IT Support Management System"
        );


        model.addAttribute(
                "systemVersion",
                "1.0.0"
        );


        model.addAttribute(
                "databaseStatus",
                "Connected"
        );


        model.addAttribute(
                "systemStatus",
                "Operational"
        );


        // =====================================================
        // OPEN SETTINGS PAGE
        // =====================================================

        return "settings";
    }
}