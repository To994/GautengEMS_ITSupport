package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import za.gov.gautengems.itsupport.entity.Ticket;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.TicketService;
import za.gov.gautengems.itsupport.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee")
public class EmployeeDashboardController {

    private final UserService userService;
    private final TicketService ticketService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EmployeeDashboardController(
            UserService userService,
            TicketService ticketService) {

        this.userService = userService;
        this.ticketService = ticketService;
    }


    // =========================================================
    // STATION MANAGER DASHBOARD
    // URL:
    //
    // /employee/dashboard
    // =========================================================

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            Authentication authentication) {


        // =====================================================
        // CHECK AUTHENTICATION
        // =====================================================

        if (authentication == null
                || authentication.getName() == null) {

            throw new RuntimeException(
                    "No authenticated user found."
            );
        }


        // =====================================================
        // GET LOGGED-IN USER
        // =====================================================

        User employee =
                userService.findByUsername(
                        authentication.getName()
                );


        if (employee == null) {

            throw new RuntimeException(
                    "Logged-in employee not found."
            );
        }


        // =====================================================
        // ONLY STATION MANAGERS
        // =====================================================

        if (employee.getRole()
                != User.Role.STATION_MANAGER) {

            throw new RuntimeException(
                    "You are not authorised to access the Station Manager dashboard."
            );
        }


        // =====================================================
        // GET ALL TICKETS
        // =====================================================

        List<Ticket> allTickets =
                ticketService.getAllTickets();


        // =====================================================
        // ONLY THIS STATION MANAGER'S TICKETS
        //
        // We identify the owner using the requester's email.
        //
        // Email is unique for every user, so this prevents
        // another Station Manager's tickets from appearing.
        // =====================================================

        String employeeEmail =
                employee.getEmail();


        List<Ticket> myTickets =
                allTickets
                        .stream()
                        .filter(ticket ->
                                ticket != null
                                        && ticket.getRequesterEmail() != null
                                        && employeeEmail != null
                                        && ticket.getRequesterEmail()
                                        .equalsIgnoreCase(
                                                employeeEmail
                                        )
                        )
                        .collect(Collectors.toList());


        // =====================================================
        // TICKET COUNTS
        // =====================================================

        long totalTickets =
                myTickets.size();


        long openTickets =
                myTickets
                        .stream()
                        .filter(ticket ->
                                "OPEN".equalsIgnoreCase(
                                        ticket.getStatus()
                                )
                        )
                        .count();


        long inProgressTickets =
                myTickets
                        .stream()
                        .filter(ticket ->
                                "IN_PROGRESS".equalsIgnoreCase(
                                        ticket.getStatus()
                                )
                        )
                        .count();


        long resolvedTickets =
                myTickets
                        .stream()
                        .filter(ticket ->
                                "RESOLVED".equalsIgnoreCase(
                                        ticket.getStatus()
                                )
                        )
                        .count();


        // =====================================================
        // ADD EMPLOYEE
        // =====================================================

        model.addAttribute(
                "employee",
                employee
        );


        // =====================================================
        // ADD ONLY MY TICKETS
        // =====================================================

        model.addAttribute(
                "tickets",
                myTickets
        );


        // =====================================================
        // STATISTICS
        // =====================================================

        model.addAttribute(
                "totalTickets",
                totalTickets
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


        // =====================================================
        // RETURN DASHBOARD
        // =====================================================

        return "employee-dashboard";
    }
}