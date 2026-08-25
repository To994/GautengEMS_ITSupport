package za.gov.gautengems.itsupport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.DistrictService;
import za.gov.gautengems.itsupport.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final DistrictService districtService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UserController(
            UserService userService,
            DistrictService districtService) {

        this.userService = userService;
        this.districtService = districtService;
    }


    // =========================================================
    // VIEW ALL USERS
    // URL: /users
    // =========================================================

    @GetMapping
    public String users(Model model) {

        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "users";
    }


    // =========================================================
    // SHOW CREATE USER FORM
    // URL: /users/new
    // =========================================================

    @GetMapping("/new")
    public String newUser(Model model) {

        User user = new User();

        model.addAttribute(
                "user",
                user
        );


        // =====================================================
        // LOAD EMS LOCATIONS
        //
        // ACTIVE DISTRICTS + THEIR STATIONS
        //
        // EMS LOCATIONS IS THE SOURCE OF TRUTH
        // =====================================================

        model.addAttribute(
                "districts",
                districtService
                        .getActiveDistrictsWithStations()
        );


        return "user-form";
    }


    // =========================================================
    // SAVE NEW USER
    // URL: /users/save
    // =========================================================

    @PostMapping("/save")
    public String saveUser(
            @ModelAttribute("user") User user) {

        userService.saveUser(user);

        return "redirect:/users";
    }


    // =========================================================
    // SHOW EDIT USER FORM
    // URL: /users/edit/{id}
    // =========================================================

    @GetMapping("/edit/{id}")
    public String editUser(
            @PathVariable Long id,
            Model model) {

        User user =
                userService.getUserById(id);

        model.addAttribute(
                "user",
                user
        );


        // =====================================================
        // LOAD EMS LOCATIONS
        //
        // SAME LOCATION SOURCE AS ADD USER
        // =====================================================

        model.addAttribute(
                "districts",
                districtService
                        .getActiveDistrictsWithStations()
        );


        return "user-form";
    }


    // =========================================================
    // UPDATE EXISTING USER
    // URL: /users/update/{id}
    // =========================================================

    @PostMapping("/update/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute("user") User user) {

        userService.updateUser(
                id,
                user
        );

        return "redirect:/users";
    }


    // =========================================================
    // DELETE USER
    // URL: /users/{id}/delete
    // =========================================================

    @PostMapping("/{id}/delete")
    public String deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return "redirect:/users";
    }

}