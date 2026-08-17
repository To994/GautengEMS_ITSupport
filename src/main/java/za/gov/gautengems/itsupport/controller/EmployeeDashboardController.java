package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import za.gov.gautengems.itsupport.entity.Ticket;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.TicketService;
import za.gov.gautengems.itsupport.service.UserService;

import java.util.List;

@Controller
public class EmployeeDashboardController {

    private final UserService userService;
    private final TicketService ticketService;

    public EmployeeDashboardController(
            UserService userService,
            TicketService ticketService) {

        this.userService = userService;
        this.ticketService = ticketService;
    }


    // =========================================================
    // EMPLOYEE DASHBOARD
    // =========================================================

    @GetMapping("/employee-dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        // -----------------------------------------------------
        // GET LOGGED-IN EMPLOYEE
        // -----------------------------------------------------

        User employee = userService
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in employee not found."
                        )
                );


        // -----------------------------------------------------
        // GET EMPLOYEE'S TICKETS
        // -----------------------------------------------------

        List<Ticket> tickets =
                ticketService.getTicketsByRequesterEmail(
                        employee.getEmail()
                );


        // -----------------------------------------------------
        // REAL TICKET COUNTS
        // -----------------------------------------------------

        long totalTickets = tickets.size();

        long openTickets = tickets.stream()
                .filter(ticket ->
                        "OPEN".equalsIgnoreCase(
                                ticket.getStatus()
                        ))
                .count();

        long inProgressTickets = tickets.stream()
                .filter(ticket ->
                        "IN_PROGRESS".equalsIgnoreCase(
                                ticket.getStatus()
                        ))
                .count();

        long resolvedTickets = tickets.stream()
                .filter(ticket ->
                        "RESOLVED".equalsIgnoreCase(
                                ticket.getStatus()
                        ))
                .count();


        // -----------------------------------------------------
        // SEND DATA TO EMPLOYEE DASHBOARD
        // -----------------------------------------------------

        model.addAttribute(
                "employee",
                employee
        );

        model.addAttribute(
                "tickets",
                tickets
        );

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


        return "employee-dashboard";
    }
}