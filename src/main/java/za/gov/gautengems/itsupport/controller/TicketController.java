package za.gov.gautengems.itsupport.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import za.gov.gautengems.itsupport.entity.Ticket;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.TicketService;
import za.gov.gautengems.itsupport.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TicketController(
            TicketService ticketService,
            UserService userService) {

        this.ticketService = ticketService;
        this.userService = userService;
    }


    // =========================================================
    // VIEW IT TICKETS
    // =========================================================

    @GetMapping
    public String tickets(
            Model model,
            Authentication authentication,
            HttpSession session) {

        User currentUser =
                getLoggedInUser(authentication);

        List<Ticket> tickets;

        boolean standbyMode = false;


        // =====================================================
        // STATION MANAGER
        //
        // ONLY THEIR OWN REPORTED TICKETS
        // =====================================================

        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            String email =
                    currentUser.getEmail();

            tickets =
                    ticketService
                            .getAllTickets()
                            .stream()
                            .filter(ticket ->
                                    ticket != null
                                            && ticket.getRequesterEmail() != null
                                            && email != null
                                            && ticket.getRequesterEmail()
                                            .equalsIgnoreCase(email))
                            .collect(Collectors.toList());
        }


        // =====================================================
        // TECHNICIAN
        // =====================================================

        else if (currentUser.getRole()
                == User.Role.TECHNICIAN) {

            standbyMode =
                    Boolean.TRUE.equals(
                            session.getAttribute(
                                    "TECHNICIAN_STANDBY"
                            )
                    );

            tickets =
                    getTechnicianVisibleTickets(
                            currentUser,
                            standbyMode
                    );
        }


        // =====================================================
        // ADMIN
        // =====================================================

        else {

            tickets =
                    ticketService.getAllTickets();
        }


        // =====================================================
        // SEND TICKETS
        // =====================================================

        model.addAttribute(
                "tickets",
                tickets
        );


        // =====================================================
        // ROLE FLAGS
        // =====================================================

        model.addAttribute(
                "isStationManager",
                currentUser.getRole()
                        == User.Role.STATION_MANAGER
        );


        model.addAttribute(
                "isTechnician",
                currentUser.getRole()
                        == User.Role.TECHNICIAN
        );


        model.addAttribute(
                "isAdmin",
                currentUser.getRole()
                        == User.Role.ADMIN
        );


        // =====================================================
        // STANDBY MODE
        // =====================================================

        model.addAttribute(
                "standbyMode",
                standbyMode
        );


        // =====================================================
        // CURRENT ROLE
        // =====================================================

        model.addAttribute(
                "currentUserRole",
                currentUser.getRole().name()
        );


        // =====================================================
        // CREATE TICKET PERMISSION
        // =====================================================

        model.addAttribute(
                "canCreateTicket",
                currentUser.getRole()
                        == User.Role.ADMIN
                        || currentUser.getRole()
                        == User.Role.STATION_MANAGER
        );


        // =====================================================
        // BACK URL
        // =====================================================

        String backUrl;


        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            backUrl =
                    "/employee/dashboard";

        } else if (currentUser.getRole()
                == User.Role.TECHNICIAN) {

            backUrl =
                    "/technician-dashboard";

        } else {

            backUrl =
                    "/dashboard";
        }


        model.addAttribute(
                "backUrl",
                backUrl
        );


        return "tickets";
    }


    // =========================================================
    // TECHNICIAN TICKET VISIBILITY
    // =========================================================

    private List<Ticket> getTechnicianVisibleTickets(
            User technician,
            boolean standbyMode) {

        String technicianName =
                technician.getFirstName()
                        + " "
                        + technician.getSurname();


        String technicianDistrict =
                normalise(
                        technician.getDistrict()
                );


        String technicianStation =
                normalise(
                        technician.getStationUnit()
                );


        return ticketService
                .getAllTickets()
                .stream()
                .filter(ticket -> {

                    if (ticket == null) {
                        return false;
                    }


                    // =================================================
                    // CHECK WHETHER ASSIGNED
                    // =================================================

                    boolean assigned =
                            ticket.getAssignedTechnician() != null
                                    && !ticket
                                    .getAssignedTechnician()
                                    .isBlank();


                    // =================================================
                    // ASSIGNED TO CURRENT TECHNICIAN
                    // =================================================

                    boolean assignedToMe =
                            assigned
                                    && ticket
                                    .getAssignedTechnician()
                                    .equalsIgnoreCase(
                                            technicianName
                                    );


                    /*
                     * A technician can ALWAYS see tickets
                     * assigned to themselves.
                     */

                    if (assignedToMe) {
                        return true;
                    }


                    /*
                     * Tickets assigned to another technician
                     * are completely hidden.
                     */

                    if (assigned) {
                        return false;
                    }


                    // =================================================
                    // FROM HERE:
                    // TICKET IS UNASSIGNED
                    // =================================================

                    String ticketDistrict =
                            normalise(
                                    ticket.getDistrict()
                            );


                    String ticketStation =
                            normalise(
                                    ticket.getStationUnit()
                            );


                    // =================================================
                    // NORMAL MODE
                    //
                    // ONLY SAME DISTRICT + SAME STATION
                    // =================================================

                    if (!standbyMode) {

                        return ticketDistrict.equals(
                                technicianDistrict
                        )
                                && ticketStation.equals(
                                technicianStation
                        );
                    }


                    // =================================================
                    // STANDBY MODE
                    //
                    // ALL UNASSIGNED TICKETS
                    // =================================================

                    return true;
                })
                .collect(Collectors.toList());
    }


    // =========================================================
    // TECHNICIAN STANDBY MODE
    // =========================================================

    @PostMapping("/standby")
    public String toggleStandby(
            Authentication authentication,
            HttpSession session) {

        User currentUser =
                getLoggedInUser(authentication);


        if (currentUser.getRole()
                != User.Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Only technicians can use standby mode."
            );
        }


        Boolean current =
                (Boolean) session.getAttribute(
                        "TECHNICIAN_STANDBY"
                );


        boolean newStatus =
                !Boolean.TRUE.equals(current);


        session.setAttribute(
                "TECHNICIAN_STANDBY",
                newStatus
        );


        return "redirect:/tickets";
    }


    // =========================================================
    // CREATE NEW TICKET PAGE
    // =========================================================

    @GetMapping("/new")
    public String newTicket(
            Model model,
            Authentication authentication) {

        User currentUser =
                getLoggedInUser(authentication);


        // =====================================================
        // TECHNICIANS CANNOT CREATE TICKETS
        // =====================================================

        if (currentUser.getRole()
                == User.Role.TECHNICIAN) {

            return "redirect:/tickets";
        }


        model.addAttribute(
                "ticket",
                new Ticket()
        );


        model.addAttribute(
                "isAdmin",
                currentUser.getRole()
                        == User.Role.ADMIN
        );


        model.addAttribute(
                "isTechnician",
                false
        );


        model.addAttribute(
                "isStationManager",
                currentUser.getRole()
                        == User.Role.STATION_MANAGER
        );


        // =====================================================
        // ADMIN TECHNICIANS
        // =====================================================

        if (currentUser.getRole()
                == User.Role.ADMIN) {

            model.addAttribute(
                    "technicians",
                    getActiveTechnicians()
            );
        }


        return "ticket-form";
    }


    // =========================================================
    // CANCEL TICKET CREATION
    // =========================================================

    @GetMapping("/cancel")
    public String cancelTicketCreation(
            Authentication authentication) {

        User currentUser =
                getLoggedInUser(authentication);


        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            return "redirect:/employee/dashboard";
        }


        if (currentUser.getRole()
                == User.Role.TECHNICIAN) {

            return "redirect:/technician-dashboard";
        }


        if (currentUser.getRole()
                == User.Role.ADMIN) {

            return "redirect:/dashboard";
        }


        return "redirect:/login";
    }


    // =========================================================
    // SAVE TICKET
    // =========================================================

    @PostMapping("/save")
    public String saveTicket(
            @ModelAttribute("ticket") Ticket ticket,
            Authentication authentication) {

        User currentUser =
                getLoggedInUser(authentication);


        // =====================================================
        // TECHNICIAN PROTECTION
        // =====================================================

        if (currentUser.getRole()
                == User.Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Technicians are not authorised to create tickets."
            );
        }


        // =====================================================
        // NEW TICKET
        // =====================================================

        if (ticket.getId() == null) {

            // =================================================
            // GENERATE TICKET NUMBER
            // =================================================

            if (ticket.getTicketNumber() == null
                    || ticket.getTicketNumber().isBlank()) {

                ticket.setTicketNumber(
                        "EMS-" + System.currentTimeMillis()
                );
            }


            // =================================================
            // STATION MANAGER
            //
            // ALL IMPORTANT INFORMATION COMES FROM
            // THE LOGGED-IN USER, NOT THE HTML FORM.
            // =================================================

            if (currentUser.getRole()
                    == User.Role.STATION_MANAGER) {

                String managerName =
                        currentUser.getFirstName()
                                + " "
                                + currentUser.getSurname();


                // =================================================
                // REQUESTER
                // =================================================

                ticket.setRequesterName(
                        managerName
                );


                ticket.setRequesterEmail(
                        currentUser.getEmail()
                );


                // =================================================
                // MANAGER INFORMATION
                // =================================================

                ticket.setManagerFirstName(
                        currentUser.getFirstName()
                );


                ticket.setManagerSurname(
                        currentUser.getSurname()
                );


                ticket.setManagerEmail(
                        currentUser.getEmail()
                );


                ticket.setManagerPhone(
                        currentUser.getPhone()
                );


                ticket.setManagerRole(
                        "STATION_MANAGER"
                );


                // =================================================
                // LOCATION
                // =================================================

                ticket.setDistrict(
                        currentUser.getDistrict()
                );


                ticket.setStationUnit(
                        currentUser.getStationUnit()
                );


                ticket.setDepartment(
                        currentUser.getDepartment()
                );


                // =================================================
                // STATION MANAGER CANNOT ASSIGN TECHNICIAN
                // =================================================

                ticket.setAssignedTechnician(null);
            }


            // =================================================
            // ADMIN
            // =================================================

            else if (currentUser.getRole()
                    == User.Role.ADMIN) {

                validateTechnicianAssignment(
                        ticket.getAssignedTechnician()
                );
            }


            // =================================================
            // SAVE
            // =================================================

            ticketService.saveTicket(ticket);


            // =================================================
            // STATION MANAGER → DASHBOARD
            // =================================================

            if (currentUser.getRole()
                    == User.Role.STATION_MANAGER) {

                return "redirect:/employee/dashboard";
            }


            return "redirect:/tickets";
        }


        // =====================================================
        // EXISTING TICKET
        // =====================================================

        Ticket existingTicket =
                ticketService
                        .getTicketById(ticket.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Ticket not found."
                                )
                        );


        // =====================================================
        // ADMIN
        // =====================================================

        if (isAdmin(authentication)) {

            validateTechnicianAssignment(
                    ticket.getAssignedTechnician()
            );


            ticketService.saveTicket(ticket);


            return "redirect:/tickets/"
                    + ticket.getId();
        }


        // =====================================================
        // STATION MANAGER
        // =====================================================

        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            if (!isOwnedByUser(
                    existingTicket,
                    currentUser)) {

                throw new RuntimeException(
                        "You are not authorised to edit this ticket."
                );
            }


            throw new RuntimeException(
                    "Station Managers are not authorised to edit tickets."
            );
        }


        // =====================================================
        // TECHNICIAN
        // =====================================================

        if (!isAssignedToUser(
                existingTicket,
                currentUser)) {

            throw new RuntimeException(
                    "You are not authorised to edit this ticket."
            );
        }


        // =====================================================
        // KEEP ORIGINAL TECHNICIAN
        // =====================================================

        ticket.setAssignedTechnician(
                existingTicket.getAssignedTechnician()
        );


        ticketService.saveTicket(ticket);


        return "redirect:/tickets/"
                + ticket.getId();
    }


    // =========================================================
    // VIEW SPECIFIC TICKET
    // =========================================================

    @GetMapping("/{id}")
    public String viewTicket(
            @PathVariable Long id,
            Model model,
            Authentication authentication,
            HttpSession session) {

        Ticket ticket =
                ticketService
                        .getTicketById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Ticket not found."
                                )
                        );


        User currentUser =
                getLoggedInUser(authentication);


        // =====================================================
        // STATION MANAGER SECURITY
        // =====================================================

        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            if (!isOwnedByUser(
                    ticket,
                    currentUser)) {

                throw new RuntimeException(
                        "Access denied."
                );
            }
        }


        // =====================================================
        // TECHNICIAN SECURITY
        // =====================================================

        if (currentUser.getRole()
                == User.Role.TECHNICIAN) {

            boolean standbyMode =
                    Boolean.TRUE.equals(
                            session.getAttribute(
                                    "TECHNICIAN_STANDBY"
                            )
                    );


            if (!canTechnicianViewTicket(
                    ticket,
                    currentUser,
                    standbyMode)) {

                throw new RuntimeException(
                        "Access denied. "
                                + "This ticket is outside your "
                                + "current work area or is assigned "
                                + "to another technician."
                );
            }
        }


        // =====================================================
        // ROLE FLAGS
        // =====================================================

        boolean admin =
                currentUser.getRole()
                        == User.Role.ADMIN;


        boolean assignedToCurrentUser =
                isAssignedToUser(
                        ticket,
                        currentUser
                );


        boolean unassigned =
                ticket.getAssignedTechnician() == null
                        || ticket.getAssignedTechnician().isBlank();


        boolean canEdit =
                admin || assignedToCurrentUser;


        boolean canTake =
                currentUser.getRole()
                        == User.Role.TECHNICIAN
                        && unassigned;


        // =====================================================
        // SEND TICKET TO VIEW
        // =====================================================

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


        model.addAttribute(
                "isStationManager",
                currentUser.getRole()
                        == User.Role.STATION_MANAGER
        );


        // =====================================================
        // BACK URL
        //
        // This is required by ticket-view.html.
        //
        // It makes the top-left arrow and the
        // "Back to My Dashboard" button functional.
        // =====================================================

        String backUrl;


        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            backUrl =
                    "/employee/dashboard";

        } else if (currentUser.getRole()
                == User.Role.TECHNICIAN) {

            backUrl =
                    "/technician-dashboard";

        } else if (currentUser.getRole()
                == User.Role.ADMIN) {

            backUrl =
                    "/dashboard";

        } else {

            backUrl =
                    "/login";
        }


        model.addAttribute(
                "backUrl",
                backUrl
        );


        return "ticket-view";
    }


    // =========================================================
    // TECHNICIAN VIEW PERMISSION
    // =========================================================

    private boolean canTechnicianViewTicket(
            Ticket ticket,
            User technician,
            boolean standbyMode) {

        if (ticket == null
                || technician == null) {

            return false;
        }


        String technicianName =
                technician.getFirstName()
                        + " "
                        + technician.getSurname();


        boolean assigned =
                ticket.getAssignedTechnician() != null
                        && !ticket
                        .getAssignedTechnician()
                        .isBlank();


        // =====================================================
        // ASSIGNED TO ME
        // =====================================================

        if (assigned
                && ticket
                .getAssignedTechnician()
                .equalsIgnoreCase(
                        technicianName
                )) {

            return true;
        }


        // =====================================================
        // ASSIGNED TO SOMEONE ELSE
        // =====================================================

        if (assigned) {

            return false;
        }


        // =====================================================
        // UNASSIGNED + STANDBY
        // =====================================================

        if (standbyMode) {

            return true;
        }


        // =====================================================
        // UNASSIGNED + NORMAL MODE
        //
        // SAME DISTRICT + SAME STATION
        // =====================================================

        return normalise(
                ticket.getDistrict()
        ).equals(
                normalise(
                        technician.getDistrict()
                )
        )
                &&

                normalise(
                        ticket.getStationUnit()
                ).equals(
                        normalise(
                                technician.getStationUnit()
                        )
                );
    }


    // =========================================================
    // EDIT TICKET
    // =========================================================

    @GetMapping("/{id}/edit")
    public String editTicket(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        Ticket ticket =
                ticketService
                        .getTicketById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Ticket not found."
                                )
                        );


        User currentUser =
                getLoggedInUser(authentication);


        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            throw new RuntimeException(
                    "Station Managers are not authorised to edit tickets."
            );
        }


        boolean admin =
                currentUser.getRole()
                        == User.Role.ADMIN;


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


        if (admin) {

            model.addAttribute(
                    "technicians",
                    getActiveTechnicians()
            );
        }


        model.addAttribute(
                "isAdmin",
                admin
        );


        model.addAttribute(
                "isTechnician",
                currentUser.getRole()
                        == User.Role.TECHNICIAN
        );


        return "ticket-edit";
    }


    // =========================================================
    // TECHNICIAN TAKES TICKET
    // =========================================================

    @PostMapping("/{id}/take")
    public String takeTicket(
            @PathVariable Long id,
            Authentication authentication,
            HttpSession session) {

        User currentUser =
                getLoggedInUser(authentication);


        // =====================================================
        // ONLY TECHNICIANS
        // =====================================================

        if (currentUser.getRole()
                != User.Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Only technicians can take tickets."
            );
        }


        Ticket ticket =
                ticketService
                        .getTicketById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Ticket not found."
                                )
                        );


        // =====================================================
        // MUST STILL BE UNASSIGNED
        // =====================================================

        if (ticket.getAssignedTechnician() != null
                && !ticket
                .getAssignedTechnician()
                .isBlank()) {

            throw new RuntimeException(
                    "This ticket has already been assigned."
            );
        }


        // =====================================================
        // CHECK TECHNICIAN'S CURRENT VISIBILITY
        //
        // This prevents a technician from manually taking
        // a ticket outside their allowed area.
        // =====================================================

        boolean standbyMode =
                Boolean.TRUE.equals(
                        session.getAttribute(
                                "TECHNICIAN_STANDBY"
                        )
                );


        if (!canTechnicianViewTicket(
                ticket,
                currentUser,
                standbyMode)) {

            throw new RuntimeException(
                    "You are not authorised to take "
                            + "this ticket."
            );
        }


        // =====================================================
        // ASSIGN TO CURRENT TECHNICIAN
        // =====================================================

        String technicianName =
                currentUser.getFirstName()
                        + " "
                        + currentUser.getSurname();


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

        if (!isAdmin(authentication)) {

            throw new RuntimeException(
                    "Only administrators can assign technicians."
            );
        }


        User selectedTechnician =
                userService.findByUsername(
                        technician
                );


        if (selectedTechnician == null) {

            throw new RuntimeException(
                    "Technician not found: "
                            + technician
            );
        }


        if (selectedTechnician.getRole()
                != User.Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Selected user is not a technician."
            );
        }


        if (!selectedTechnician.isActive()) {

            throw new RuntimeException(
                    "Selected technician is not active."
            );
        }


        String technicianName =
                selectedTechnician.getFirstName()
                        + " "
                        + selectedTechnician.getSurname();


        ticketService.assignTicket(
                id,
                technicianName
        );


        return "redirect:/tickets/" + id;
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {

        Ticket ticket =
                ticketService
                        .getTicketById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Ticket not found."
                                )
                        );


        User currentUser =
                getLoggedInUser(authentication);


        // =====================================================
        // STATION MANAGER CANNOT UPDATE
        // =====================================================

        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            throw new RuntimeException(
                    "Station Managers are not authorised "
                            + "to update ticket status."
            );
        }


        boolean admin =
                currentUser.getRole()
                        == User.Role.ADMIN;


        boolean assigned =
                isAssignedToUser(
                        ticket,
                        currentUser
                );


        if (!admin && !assigned) {

            throw new RuntimeException(
                    "You are not authorised to update this ticket."
            );
        }


        if (!isValidStatus(status)) {

            throw new IllegalArgumentException(
                    "Invalid ticket status."
            );
        }


        ticket.setStatus(status);

        ticketService.saveTicket(ticket);


        return "redirect:/tickets/" + id;
    }


    // =========================================================
    // UPDATE PRIORITY
    // =========================================================

    @PostMapping("/{id}/priority")
    public String updatePriority(
            @PathVariable Long id,
            @RequestParam String priority,
            Authentication authentication) {

        Ticket ticket =
                ticketService
                        .getTicketById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Ticket not found."
                                )
                        );


        User currentUser =
                getLoggedInUser(authentication);


        // =====================================================
        // STATION MANAGER CANNOT UPDATE
        // =====================================================

        if (currentUser.getRole()
                == User.Role.STATION_MANAGER) {

            throw new RuntimeException(
                    "Station Managers are not authorised "
                            + "to update ticket priority."
            );
        }


        boolean admin =
                currentUser.getRole()
                        == User.Role.ADMIN;


        boolean assigned =
                isAssignedToUser(
                        ticket,
                        currentUser
                );


        if (!admin && !assigned) {

            throw new RuntimeException(
                    "You are not authorised to update this ticket."
            );
        }


        if (!isValidPriority(priority)) {

            throw new IllegalArgumentException(
                    "Invalid ticket priority."
            );
        }


        ticket.setPriority(priority);

        ticketService.saveTicket(ticket);


        return "redirect:/tickets/" + id;
    }


    // =========================================================
    // DELETE
    // =========================================================

    @PostMapping("/{id}/delete")
    public String deleteTicket(
            @PathVariable Long id,
            Authentication authentication) {

        if (!isAdmin(authentication)) {

            throw new RuntimeException(
                    "Only administrators can delete tickets."
            );
        }


        ticketService.deleteTicket(id);


        return "redirect:/tickets";
    }


    // =========================================================
    // ACTIVE TECHNICIANS
    // =========================================================

    private List<User> getActiveTechnicians() {

        return userService
                .getAllUsers()
                .stream()
                .filter(user ->
                        user != null
                                && user.getRole()
                                == User.Role.TECHNICIAN
                                && user.isActive()
                )
                .collect(Collectors.toList());
    }


    // =========================================================
    // VALIDATE TECHNICIAN
    // =========================================================

    private void validateTechnicianAssignment(
            String assignedTechnician) {

        if (assignedTechnician == null
                || assignedTechnician.isBlank()) {

            return;
        }


        boolean validTechnician =
                getActiveTechnicians()
                        .stream()
                        .anyMatch(technician -> {

                            String fullName =
                                    technician.getFirstName()
                                            + " "
                                            + technician.getSurname();


                            return fullName.equalsIgnoreCase(
                                    assignedTechnician.trim()
                            );
                        });


        if (!validTechnician) {

            throw new RuntimeException(
                    "Selected technician is invalid or inactive."
            );
        }
    }


    // =========================================================
    // VALID STATUS
    // =========================================================

    private boolean isValidStatus(
            String status) {

        if (status == null) {
            return false;
        }


        return status.equals("OPEN")
                || status.equals("PENDING")
                || status.equals("IN_PROGRESS")
                || status.equals("RESOLVED")
                || status.equals("CLOSED");
    }


    // =========================================================
    // VALID PRIORITY
    // =========================================================

    private boolean isValidPriority(
            String priority) {

        if (priority == null) {
            return false;
        }


        return priority.equals("LOW")
                || priority.equals("MEDIUM")
                || priority.equals("HIGH")
                || priority.equals("URGENT")
                || priority.equals("CRITICAL");
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

        if (authentication == null
                || authentication.getName() == null) {

            throw new RuntimeException(
                    "No authenticated user found."
            );
        }


        User user =
                userService.findByUsername(
                        authentication.getName()
                );


        if (user == null) {

            throw new RuntimeException(
                    "Logged-in user not found: "
                            + authentication.getName()
            );
        }


        return user;
    }


    // =========================================================
    // CHECK TECHNICIAN ASSIGNMENT
    // =========================================================

    private boolean isAssignedToUser(
            Ticket ticket,
            User user) {

        if (ticket == null
                || user == null) {

            return false;
        }


        if (ticket.getAssignedTechnician() == null
                || ticket.getAssignedTechnician().isBlank()) {

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


    // =========================================================
    // CHECK TICKET OWNER
    // =========================================================

    private boolean isOwnedByUser(
            Ticket ticket,
            User user) {

        if (ticket == null
                || user == null) {

            return false;
        }


        if (ticket.getRequesterEmail() == null
                || user.getEmail() == null) {

            return false;
        }


        return ticket
                .getRequesterEmail()
                .equalsIgnoreCase(
                        user.getEmail()
                );
    }


    // =========================================================
    // NORMALISE LOCATION
    // =========================================================

    private String normalise(
            String value) {

        if (value == null) {
            return "";
        }


        return value
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }
}