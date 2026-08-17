package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import za.gov.gautengems.itsupport.entity.Ticket;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.TicketService;
import za.gov.gautengems.itsupport.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/technicians")
public class TechnicianController {

    private final UserService userService;
    private final TicketService ticketService;

    public TechnicianController(
            UserService userService,
            TicketService ticketService) {

        this.userService = userService;
        this.ticketService = ticketService;
    }


    // =========================================================
    // TECHNICIANS PAGE
    // URL: /technicians
    // =========================================================

    @GetMapping
    public String technicians(
            Model model,
            Authentication authentication) {

        // -----------------------------------------------------
        // SECURITY
        // Only ADMIN can access technician management
        // -----------------------------------------------------

        if (!isAdmin(authentication)) {
            return "redirect:/dashboard";
        }


        // -----------------------------------------------------
        // GET ALL TECHNICIANS
        // -----------------------------------------------------

        List<User> technicians =
                userService.getAllUsers()
                        .stream()
                        .filter(user ->
                                user.getRole()
                                        == User.Role.TECHNICIAN)
                        .toList();


        // -----------------------------------------------------
        // GET ALL TICKETS
        // -----------------------------------------------------

        List<Ticket> allTickets =
                ticketService.getAllTickets();


        // -----------------------------------------------------
        // COUNTS
        // -----------------------------------------------------

        long activeTechnicians =
                technicians.stream()
                        .filter(User::isActive)
                        .count();


        long inactiveTechnicians =
                technicians.stream()
                        .filter(user -> !user.isActive())
                        .count();


        long assignedTickets =
                allTickets.stream()
                        .filter(ticket ->
                                ticket.getAssignedTechnician() != null
                                        && !ticket
                                        .getAssignedTechnician()
                                        .isBlank())
                        .count();


        long unassignedTickets =
                allTickets.stream()
                        .filter(ticket ->
                                ticket.getAssignedTechnician() == null
                                        || ticket
                                        .getAssignedTechnician()
                                        .isBlank())
                        .count();


        // -----------------------------------------------------
        // SEND DATA TO technicians.html
        // -----------------------------------------------------

        model.addAttribute(
                "technicians",
                technicians
        );

        model.addAttribute(
                "activeTechnicians",
                activeTechnicians
        );

        model.addAttribute(
                "inactiveTechnicians",
                inactiveTechnicians
        );

        model.addAttribute(
                "assignedTickets",
                assignedTickets
        );

        model.addAttribute(
                "unassignedTickets",
                unassignedTickets
        );


        return "technicians";
    }


    // =========================================================
    // VIEW TECHNICIAN DETAILS
    // URL: /technicians/{id}
    // =========================================================

    @GetMapping("/{id}")
    public String technicianDetails(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        // -----------------------------------------------------
        // SECURITY
        // Only ADMIN can view technician details
        // -----------------------------------------------------

        if (!isAdmin(authentication)) {
            return "redirect:/dashboard";
        }


        // -----------------------------------------------------
        // FIND TECHNICIAN
        // -----------------------------------------------------

        User technician =
                userService.getAllUsers()
                        .stream()
                        .filter(user ->
                                user.getId().equals(id)
                                        && user.getRole()
                                        == User.Role.TECHNICIAN)
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Technician not found."
                                ));


        // -----------------------------------------------------
        // TECHNICIAN FULL NAME
        // -----------------------------------------------------

        String technicianName =
                technician.getFirstName()
                        + " "
                        + technician.getSurname();


        // -----------------------------------------------------
        // GET ALL TICKETS ASSIGNED TO THIS TECHNICIAN
        // -----------------------------------------------------

        List<Ticket> assignedTickets =
                ticketService.getAllTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getAssignedTechnician() != null
                                        && !ticket
                                        .getAssignedTechnician()
                                        .isBlank()
                                        && ticket
                                        .getAssignedTechnician()
                                        .equalsIgnoreCase(
                                                technicianName
                                        ))
                        .toList();


        // -----------------------------------------------------
        // TOTAL ASSIGNED TICKETS
        // -----------------------------------------------------

        long totalAssignedTickets =
                assignedTickets.size();


        // -----------------------------------------------------
        // OPEN TICKETS
        // -----------------------------------------------------

        long openTickets =
                assignedTickets.stream()
                        .filter(ticket ->
                                "OPEN".equalsIgnoreCase(
                                        ticket.getStatus()))
                        .count();


        // -----------------------------------------------------
        // PENDING TICKETS
        // -----------------------------------------------------

        long pendingTickets =
                assignedTickets.stream()
                        .filter(ticket ->
                                "PENDING".equalsIgnoreCase(
                                        ticket.getStatus()))
                        .count();


        // -----------------------------------------------------
        // IN PROGRESS TICKETS
        // -----------------------------------------------------

        long inProgressTickets =
                assignedTickets.stream()
                        .filter(ticket ->
                                "IN_PROGRESS".equalsIgnoreCase(
                                        ticket.getStatus()))
                        .count();


        // -----------------------------------------------------
        // RESOLVED TICKETS
        // -----------------------------------------------------

        long resolvedTickets =
                assignedTickets.stream()
                        .filter(ticket ->
                                "RESOLVED".equalsIgnoreCase(
                                        ticket.getStatus()))
                        .count();


        // -----------------------------------------------------
        // CLOSED TICKETS
        // -----------------------------------------------------

        long closedTickets =
                assignedTickets.stream()
                        .filter(ticket ->
                                "CLOSED".equalsIgnoreCase(
                                        ticket.getStatus()))
                        .count();


        // -----------------------------------------------------
        // ACTIVE WORKLOAD
        // OPEN + PENDING + IN PROGRESS
        // -----------------------------------------------------

        long activeWorkload =
                openTickets
                        + pendingTickets
                        + inProgressTickets;


        // -----------------------------------------------------
        // SEND TECHNICIAN DATA TO HTML
        // -----------------------------------------------------

        model.addAttribute(
                "technician",
                technician
        );


        model.addAttribute(
                "assignedTickets",
                assignedTickets
        );


        model.addAttribute(
                "totalAssignedTickets",
                totalAssignedTickets
        );


        model.addAttribute(
                "openTickets",
                openTickets
        );


        model.addAttribute(
                "pendingTickets",
                pendingTickets
        );


        model.addAttribute(
                "inProgressTickets",
                inProgressTickets
        );


        model.addAttribute(
                "resolvedTickets",
                resolvedTickets
        );


        model.addAttribute(
                "closedTickets",
                closedTickets
        );


        model.addAttribute(
                "activeWorkload",
                activeWorkload
        );


        return "technician-details";
    }


    // =========================================================
    // CHECK ADMIN
    // =========================================================

    private boolean isAdmin(
            Authentication authentication) {

        if (authentication == null) {
            return false;
        }


        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN")
                );
    }
}