package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.TechnicianAttendanceService;
import za.gov.gautengems.itsupport.service.TicketService;
import za.gov.gautengems.itsupport.service.UserService;

@Controller
@RequestMapping("/technician-dashboard")
public class TechnicianDashboardController {

    private final UserService userService;
    private final TechnicianAttendanceService attendanceService;
    private final TicketService ticketService;

    public TechnicianDashboardController(
            UserService userService,
            TechnicianAttendanceService attendanceService,
            TicketService ticketService) {

        this.userService = userService;
        this.attendanceService = attendanceService;
        this.ticketService = ticketService;
    }


    // =========================================================
    // TECHNICIAN DASHBOARD
    // =========================================================

    @GetMapping
    public String dashboard(
            Authentication authentication,
            Model model) {

        User technician =
                getLoggedInTechnician(authentication);


        // =====================================================
        // TECHNICIAN INFORMATION
        // =====================================================

        model.addAttribute(
                "technician",
                technician
        );


        // =====================================================
        // TODAY'S ATTENDANCE
        // =====================================================

        model.addAttribute(
                "attendance",
                attendanceService
                        .getTodayAttendance(technician)
                        .orElse(null)
        );


        // =====================================================
        // TECHNICIAN NAME
        // =====================================================

        String technicianName =
                technician.getFirstName()
                        + " "
                        + technician.getSurname();


        // =====================================================
        // REAL TICKET COUNTS
        // =====================================================

        // Total tickets assigned to this technician
        long assignedTickets =
                ticketService.countByTechnician(
                        technicianName
                );


        // Open tickets
        long openTickets =
                ticketService.countByTechnicianAndStatus(
                        technicianName,
                        "OPEN"
                );


        // In-progress tickets
        long inProgressTickets =
                ticketService.countByTechnicianAndStatus(
                        technicianName,
                        "IN_PROGRESS"
                );


        // Resolved tickets
        long resolvedTickets =
                ticketService.countByTechnicianAndStatus(
                        technicianName,
                        "RESOLVED"
                );


        // =====================================================
        // SEND COUNTS TO THYMELEAF
        // =====================================================

        model.addAttribute(
                "assignedTickets",
                assignedTickets
        );

        model.addAttribute(
                "openTickets",
                openTickets
        );

        model.addAttribute(
                "inProgressTickets",
                inProgressTickets
        );

        model.addAttribute(
                "resolvedTickets",
                resolvedTickets
        );


        return "technician-dashboard";
    }


    // =========================================================
    // CHECK IN
    // =========================================================

    @PostMapping("/check-in")
    public String checkIn(
            Authentication authentication) {

        User technician =
                getLoggedInTechnician(authentication);

        attendanceService.checkIn(technician);

        return "redirect:/technician-dashboard";
    }


    // =========================================================
    // START LUNCH
    // =========================================================

    @PostMapping("/lunch")
    public String startLunch(
            Authentication authentication) {

        User technician =
                getLoggedInTechnician(authentication);

        attendanceService.startLunch(technician);

        return "redirect:/technician-dashboard";
    }


    // =========================================================
    // RETURN FROM LUNCH
    // =========================================================

    @PostMapping("/return")
    public String returnFromLunch(
            Authentication authentication) {

        User technician =
                getLoggedInTechnician(authentication);

        attendanceService.endLunch(technician);

        return "redirect:/technician-dashboard";
    }


    // =========================================================
    // CHECK OUT
    // =========================================================

    @PostMapping("/check-out")
    public String checkOut(
            Authentication authentication) {

        User technician =
                getLoggedInTechnician(authentication);

        attendanceService.checkOut(technician);

        return "redirect:/technician-dashboard";
    }


    // =========================================================
    // FIND LOGGED-IN TECHNICIAN
    // =========================================================

    private User getLoggedInTechnician(
            Authentication authentication) {

        return userService
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in technician not found."
                        )
                );
    }
}