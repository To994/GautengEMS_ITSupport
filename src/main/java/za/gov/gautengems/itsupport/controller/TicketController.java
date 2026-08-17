package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import za.gov.gautengems.itsupport.entity.Ticket;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.TicketService;
import za.gov.gautengems.itsupport.service.UserService;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(
            TicketService ticketService,
            UserService userService) {

        this.ticketService = ticketService;
        this.userService = userService;
    }


    // =========================================================
    // VIEW ALL TICKETS
    // =========================================================

    @GetMapping
    public String tickets(Model model) {

        model.addAttribute(
                "tickets",
                ticketService.getAllTickets()
        );

        return "tickets";
    }


    // =========================================================
    // CREATE NEW TICKET
    // =========================================================

    @GetMapping("/new")
    public String newTicket(Model model) {

        model.addAttribute(
                "ticket",
                new Ticket()
        );

        return "ticket-form";
    }


    // =========================================================
    // SAVE NEW OR UPDATED TICKET
    // =========================================================

    @PostMapping("/save")
    public String saveTicket(
            @ModelAttribute Ticket ticket,
            Authentication authentication) {

        // -----------------------------------------------------
        // NEW TICKET
        // -----------------------------------------------------

        if (ticket.getId() == null) {

            if (ticket.getTicketNumber() == null ||
                    ticket.getTicketNumber().isBlank()) {

                ticket.setTicketNumber(
                        "EMS-" + System.currentTimeMillis()
                );
            }

            ticketService.saveTicket(ticket);

            return "redirect:/tickets";
        }


        // -----------------------------------------------------
        // EXISTING TICKET
        // -----------------------------------------------------

        Ticket existingTicket = ticketService
                .getTicketById(ticket.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found"
                        ));


        // -----------------------------------------------------
        // ADMIN CAN EDIT ANY TICKET
        // -----------------------------------------------------

        if (isAdmin(authentication)) {

            ticketService.saveTicket(ticket);

            return "redirect:/tickets/" + ticket.getId();
        }


        // -----------------------------------------------------
        // TECHNICIAN
        // -----------------------------------------------------

        User currentUser =
                getLoggedInUser(authentication);

        if (currentUser.getRole()
                != User.Role.TECHNICIAN) {

            throw new RuntimeException(
                    "You are not authorised to edit this ticket."
            );
        }


        // -----------------------------------------------------
        // TECHNICIAN MUST BE ASSIGNED
        // -----------------------------------------------------

        if (!isAssignedToUser(
                existingTicket,
                currentUser)) {

            throw new RuntimeException(
                    "You are not assigned to this ticket."
            );
        }


        // -----------------------------------------------------
        // KEEP ORIGINAL ASSIGNMENT
        // -----------------------------------------------------

        ticket.setAssignedTechnician(
                existingTicket.getAssignedTechnician()
        );


        ticketService.saveTicket(ticket);

        return "redirect:/tickets/" + ticket.getId();
    }


    // =========================================================
    // VIEW SPECIFIC TICKET
    // =========================================================

    @GetMapping("/{id}")
    public String viewTicket(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        Ticket ticket = ticketService
                .getTicketById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found"
                        ));


        User currentUser =
                getLoggedInUser(authentication);


        // -----------------------------------------------------
        // CHECK ADMIN
        // -----------------------------------------------------

        boolean admin =
                currentUser.getRole()
                        == User.Role.ADMIN;


        // -----------------------------------------------------
        // CHECK ASSIGNMENT
        // -----------------------------------------------------

        boolean assignedToCurrentUser =
                isAssignedToUser(
                        ticket,
                        currentUser
                );


        // -----------------------------------------------------
        // CHECK IF UNASSIGNED
        // -----------------------------------------------------

        boolean unassigned =
                ticket.getAssignedTechnician() == null
                        ||
                        ticket.getAssignedTechnician().isBlank();


        // -----------------------------------------------------
        // PERMISSIONS
        // -----------------------------------------------------

        boolean canEdit =
                admin || assignedToCurrentUser;


        boolean canTake =
                currentUser.getRole()
                        == User.Role.TECHNICIAN
                        && unassigned;


        // -----------------------------------------------------
        // SEND DATA TO THYMELEAF
        // -----------------------------------------------------

        model.addAttribute(
                "ticket",
                ticket
        );


        model.addAttribute(
                "canEdit",
                canEdit
        );


        model.addAttribute(
                "canTake",
                canTake
        );


        model.addAttribute(
                "isAdmin",
                admin
        );


        return "ticket-view";
    }


    // =========================================================
    // EDIT TICKET
    // =========================================================

    @GetMapping("/{id}/edit")
    public String editTicket(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        Ticket ticket = ticketService
                .getTicketById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found"
                        ));


        User currentUser =
                getLoggedInUser(authentication);


        // -----------------------------------------------------
        // ADMIN CAN EDIT ANYTHING
        // -----------------------------------------------------

        boolean admin =
                currentUser.getRole()
                        == User.Role.ADMIN;


        // -----------------------------------------------------
        // TECHNICIAN MUST BE ASSIGNED
        // -----------------------------------------------------

        boolean assigned =
                isAssignedToUser(
                        ticket,
                        currentUser
                );


        if (!admin && !assigned) {

            throw new RuntimeException(
                    "You are not authorised to edit this ticket."
            );
        }


        model.addAttribute(
                "ticket",
                ticket
        );


        // -----------------------------------------------------
        // ACTIVE TECHNICIANS
        // -----------------------------------------------------

        model.addAttribute(
                "technicians",
                userService.getActiveTechnicians()
        );


        model.addAttribute(
                "isAdmin",
                admin
        );


        return "ticket-edit";
    }


    // =========================================================
    // TECHNICIAN TAKES UNASSIGNED TICKET
    // =========================================================

    @PostMapping("/{id}/take")
    public String takeTicket(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser =
                getLoggedInUser(authentication);


        // -----------------------------------------------------
        // ONLY TECHNICIANS
        // -----------------------------------------------------

        if (currentUser.getRole()
                != User.Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Only technicians can take tickets."
            );
        }


        // -----------------------------------------------------
        // TECHNICIAN NAME
        // -----------------------------------------------------

        String technicianName =
                currentUser.getFirstName()
                        + " "
                        + currentUser.getSurname();


        // -----------------------------------------------------
        // TAKE TICKET
        // -----------------------------------------------------

        ticketService.takeTicket(
                id,
                technicianName
        );


        return "redirect:/tickets/" + id;
    }


    // =========================================================
    // ADMIN ASSIGNS TECHNICIAN
    // =========================================================

    @PostMapping("/{id}/assign")
    public String assignTicket(
            @PathVariable Long id,
            @RequestParam String technician,
            Authentication authentication) {

        // -----------------------------------------------------
        // ONLY ADMIN
        // -----------------------------------------------------

        if (!isAdmin(authentication)) {

            throw new RuntimeException(
                    "Only administrators can assign technicians."
            );
        }


        // -----------------------------------------------------
        // FIND TECHNICIAN
        // -----------------------------------------------------

        User selectedTechnician =
                userService
                        .findByUsername(technician)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Technician not found"
                                ));


        // -----------------------------------------------------
        // CHECK ROLE
        // -----------------------------------------------------

        if (selectedTechnician.getRole()
                != User.Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Selected user is not a technician."
            );
        }


        // -----------------------------------------------------
        // CHECK ACTIVE
        // -----------------------------------------------------

        if (!selectedTechnician.isActive()) {

            throw new RuntimeException(
                    "Selected technician is not active."
            );
        }


        // -----------------------------------------------------
        // TECHNICIAN FULL NAME
        // -----------------------------------------------------

        String technicianName =
                selectedTechnician.getFirstName()
                        + " "
                        + selectedTechnician.getSurname();


        // -----------------------------------------------------
        // ASSIGN
        // -----------------------------------------------------

        ticketService.assignTicket(
                id,
                technicianName
        );


        return "redirect:/tickets/" + id;
    }


    // =========================================================
    // UPDATE TICKET STATUS
    // =========================================================

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {

        // -----------------------------------------------------
        // FIND TICKET
        // -----------------------------------------------------

        Ticket ticket = ticketService
                .getTicketById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found"
                        ));


        // -----------------------------------------------------
        // GET CURRENT USER
        // -----------------------------------------------------

        User currentUser =
                getLoggedInUser(authentication);


        // -----------------------------------------------------
        // CHECK ADMIN
        // -----------------------------------------------------

        boolean admin =
                currentUser.getRole()
                        == User.Role.ADMIN;


        // -----------------------------------------------------
        // CHECK TECHNICIAN ASSIGNMENT
        // -----------------------------------------------------

        boolean assigned =
                isAssignedToUser(
                        ticket,
                        currentUser
                );


        // -----------------------------------------------------
        // AUTHORISATION
        // -----------------------------------------------------

        if (!admin && !assigned) {

            throw new RuntimeException(
                    "You are not authorised to update this ticket."
            );
        }


        // -----------------------------------------------------
        // VALIDATE STATUS
        // -----------------------------------------------------

        if (!isValidStatus(status)) {

            throw new IllegalArgumentException(
                    "Invalid ticket status."
            );
        }


        // -----------------------------------------------------
        // UPDATE STATUS
        // -----------------------------------------------------

        ticket.setStatus(status);

        ticketService.saveTicket(ticket);


        // -----------------------------------------------------
        // RETURN TO TICKET
        // -----------------------------------------------------

        return "redirect:/tickets/" + id;
    }


    // =========================================================
    // UPDATE TICKET PRIORITY
    // =========================================================

    @PostMapping("/{id}/priority")
    public String updatePriority(
            @PathVariable Long id,
            @RequestParam String priority,
            Authentication authentication) {

        // -----------------------------------------------------
        // FIND TICKET
        // -----------------------------------------------------

        Ticket ticket = ticketService
                .getTicketById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found"
                        ));


        // -----------------------------------------------------
        // GET CURRENT USER
        // -----------------------------------------------------

        User currentUser =
                getLoggedInUser(authentication);


        // -----------------------------------------------------
        // CHECK ADMIN
        // -----------------------------------------------------

        boolean admin =
                currentUser.getRole()
                        == User.Role.ADMIN;


        // -----------------------------------------------------
        // CHECK TECHNICIAN ASSIGNMENT
        // -----------------------------------------------------

        boolean assigned =
                isAssignedToUser(
                        ticket,
                        currentUser
                );


        // -----------------------------------------------------
        // AUTHORISATION
        // -----------------------------------------------------

        if (!admin && !assigned) {

            throw new RuntimeException(
                    "You are not authorised to update this ticket."
            );
        }


        // -----------------------------------------------------
        // VALIDATE PRIORITY
        // -----------------------------------------------------

        if (!isValidPriority(priority)) {

            throw new IllegalArgumentException(
                    "Invalid ticket priority."
            );
        }


        // -----------------------------------------------------
        // UPDATE PRIORITY
        // -----------------------------------------------------

        ticket.setPriority(priority);

        ticketService.saveTicket(ticket);


        // -----------------------------------------------------
        // RETURN TO TICKET
        // -----------------------------------------------------

        return "redirect:/tickets/" + id;
    }


    // =========================================================
    // DELETE TICKET
    // =========================================================

    @PostMapping("/{id}/delete")
    public String deleteTicket(
            @PathVariable Long id,
            Authentication authentication) {

        // -----------------------------------------------------
        // ONLY ADMIN
        // -----------------------------------------------------

        if (!isAdmin(authentication)) {

            throw new RuntimeException(
                    "Only administrators can delete tickets."
            );
        }


        ticketService.deleteTicket(id);

        return "redirect:/tickets";
    }


    // =========================================================
    // VALIDATE STATUS
    // =========================================================

    private boolean isValidStatus(
            String status) {

        if (status == null) {
            return false;
        }

        return status.equals("OPEN")
                || status.equals("PENDING")
                || status.equals("IN_PROGRESS")
                || status.equals("RESOLVED");
    }


    // =========================================================
    // VALIDATE PRIORITY
    // =========================================================

    private boolean isValidPriority(
            String priority) {

        if (priority == null) {
            return false;
        }

        return priority.equals("LOW")
                || priority.equals("MEDIUM")
                || priority.equals("HIGH")
                || priority.equals("URGENT");
    }


    // =========================================================
    // CHECK ADMIN
    // =========================================================

    private boolean isAdmin(
            Authentication authentication) {

        if (authentication == null) {
            return false;
        }

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN")
                );
    }


    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private User getLoggedInUser(
            Authentication authentication) {

        if (authentication == null ||
                authentication.getName() == null) {

            throw new RuntimeException(
                    "No authenticated user found."
            );
        }

        return userService
                .findByUsername(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found."
                        ));
    }


    // =========================================================
    // CHECK TICKET ASSIGNMENT
    // =========================================================

    private boolean isAssignedToUser(
            Ticket ticket,
            User user) {

        if (ticket == null ||
                user == null) {

            return false;
        }


        if (ticket.getAssignedTechnician() == null ||
                ticket.getAssignedTechnician().isBlank()) {

            return false;
        }


        String fullName =
                user.getFirstName()
                        + " "
                        + user.getSurname();


        return ticket
                .getAssignedTechnician()
                .equalsIgnoreCase(fullName);
    }
}