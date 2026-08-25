package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import za.gov.gautengems.itsupport.entity.Ticket;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.service.TechnicianAttendanceService;
import za.gov.gautengems.itsupport.service.TicketService;
import za.gov.gautengems.itsupport.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/technician-dashboard")
public class TechnicianDashboardController {

    private final UserService userService;
    private final TechnicianAttendanceService attendanceService;
    private final TicketService ticketService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

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

        long assignedTickets =
                ticketService.countByTechnician(
                        technicianName
                );


        long openTickets =
                ticketService.countByTechnicianAndStatus(
                        technicianName,
                        "OPEN"
                );


        long inProgressTickets =
                ticketService.countByTechnicianAndStatus(
                        technicianName,
                        "IN_PROGRESS"
                );


        long resolvedTickets =
                ticketService.countByTechnicianAndStatus(
                        technicianName,
                        "RESOLVED"
                );


        // =====================================================
        // SEND COUNTS TO DASHBOARD
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
    // MY WORK OVERVIEW
    //
    // ONLY THE CURRENTLY LOGGED-IN TECHNICIAN
    // =========================================================

    @GetMapping("/my-work")
    public String myWorkOverview(
            Authentication authentication,
            Model model) {


        // =====================================================
        // GET LOGGED-IN TECHNICIAN
        // =====================================================

        User technician =
                getLoggedInTechnician(authentication);


        // =====================================================
        // TECHNICIAN NAME
        // =====================================================

        String technicianName =
                technician.getFirstName()
                        + " "
                        + technician.getSurname();


        // =====================================================
        // ONLY THIS TECHNICIAN'S TICKETS
        // =====================================================

        List<Ticket> myTickets =
                ticketService.getTicketsByTechnician(
                        technicianName
                );


        // =====================================================
        // PERSONAL TICKET COUNTS
        // =====================================================

        long totalTickets =
                myTickets.size();


        long openTickets = 0;

        long inProgressTickets = 0;

        long resolvedTickets = 0;


        // =====================================================
        // PRIORITY COUNTS
        // =====================================================

        long highPriorityTickets = 0;

        long mediumPriorityTickets = 0;

        long lowPriorityTickets = 0;

        long urgentPriorityTickets = 0;


        // =====================================================
        // CALCULATE PERSONAL STATISTICS
        // =====================================================

        for (Ticket ticket : myTickets) {


            // -------------------------------------------------
            // STATUS
            // -------------------------------------------------

            if (ticket.getStatus() != null) {

                String status =
                        ticket.getStatus()
                                .trim()
                                .toUpperCase();

                switch (status) {

                    case "OPEN":
                        openTickets++;
                        break;

                    case "IN_PROGRESS":
                        inProgressTickets++;
                        break;

                    case "RESOLVED":
                        resolvedTickets++;
                        break;

                    default:
                        break;
                }
            }


            // -------------------------------------------------
            // PRIORITY
            // -------------------------------------------------

            if (ticket.getPriority() != null) {

                String priority =
                        ticket.getPriority()
                                .trim()
                                .toUpperCase();

                switch (priority) {

                    case "URGENT":
                        urgentPriorityTickets++;
                        break;

                    case "HIGH":
                        highPriorityTickets++;
                        break;

                    case "MEDIUM":
                        mediumPriorityTickets++;
                        break;

                    case "LOW":
                        lowPriorityTickets++;
                        break;

                    default:
                        break;
                }
            }
        }


        // =====================================================
        // UNRESOLVED
        // =====================================================

        long unresolvedTickets =
                openTickets
                        + inProgressTickets;


        // =====================================================
        // RESOLUTION RATE
        // =====================================================

        double resolutionRate = 0;

        if (totalTickets > 0) {

            resolutionRate =
                    ((double) resolvedTickets
                            / totalTickets)
                            * 100;
        }


        long performancePercentage =
                Math.round(resolutionRate);


        // =====================================================
        // PERFORMANCE MESSAGE
        // =====================================================

        String performanceTitle;

        String performanceMessage;


        if (totalTickets == 0) {

            performanceTitle =
                    "Ready to Get Started";

            performanceMessage =
                    "You currently have no tickets assigned to you. "
                            + "Keep checking View Tickets and take available "
                            + "support requests when they are assigned to you.";


        } else if (performancePercentage >= 80) {

            performanceTitle =
                    "Excellent Work!";

            performanceMessage =
                    "You're doing an excellent job. "
                            + "Your resolved-ticket rate shows strong progress "
                            + "and good ticket completion.";


        } else if (performancePercentage >= 60) {

            performanceTitle =
                    "Good Progress";

            performanceMessage =
                    "You're making good progress. "
                            + "Keep working through your outstanding tickets "
                            + "and aim to improve your resolution rate.";


        } else if (performancePercentage >= 40) {

            performanceTitle =
                    "Keep Pushing";

            performanceMessage =
                    "You have made progress, but there is still work "
                            + "to complete. Focus on your open and in-progress "
                            + "tickets and keep pushing toward resolution.";


        } else {

            performanceTitle =
                    "More Attention Needed";

            performanceMessage =
                    "Your current resolution rate is low. "
                            + "Review your outstanding tickets and focus on "
                            + "closing tickets that are still in progress.";
        }


        // =====================================================
        // RESOLVED TICKETS OVER THE LAST 6 MONTHS
        //
        // Uses updatedAt for tickets whose current status
        // is RESOLVED.
        // =====================================================

        List<String> resolvedMonthLabels =
                new ArrayList<>();

        List<Long> resolvedMonthValues =
                new ArrayList<>();


        YearMonth currentMonth =
                YearMonth.now();


        DateTimeFormatter monthFormatter =
                DateTimeFormatter.ofPattern("MMM yyyy");


        for (int i = 5; i >= 0; i--) {

            YearMonth month =
                    currentMonth.minusMonths(i);


            resolvedMonthLabels.add(
                    month.format(monthFormatter)
            );


            long count = 0;


            for (Ticket ticket : myTickets) {

                if (ticket.getStatus() == null) {
                    continue;
                }


                if (!"RESOLVED".equalsIgnoreCase(
                        ticket.getStatus())) {

                    continue;
                }


                LocalDateTime updatedAt =
                        ticket.getUpdatedAt();


                if (updatedAt == null) {
                    continue;
                }


                YearMonth ticketMonth =
                        YearMonth.from(updatedAt);


                if (ticketMonth.equals(month)) {

                    count++;
                }
            }


            resolvedMonthValues.add(count);
        }


        // =====================================================
        // SEND ALL PERSONAL DATA TO THYMELEAF
        // =====================================================

        model.addAttribute(
                "technician",
                technician
        );


        model.addAttribute(
                "technicianName",
                technicianName
        );


        model.addAttribute(
                "myTickets",
                myTickets
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


        model.addAttribute(
                "unresolvedTickets",
                unresolvedTickets
        );


        model.addAttribute(
                "resolutionRate",
                resolutionRate
        );


        model.addAttribute(
                "performancePercentage",
                performancePercentage
        );


        model.addAttribute(
                "performanceTitle",
                performanceTitle
        );


        model.addAttribute(
                "performanceMessage",
                performanceMessage
        );


        // =====================================================
        // PRIORITY DATA
        // =====================================================

        model.addAttribute(
                "highPriorityTickets",
                highPriorityTickets
        );


        model.addAttribute(
                "mediumPriorityTickets",
                mediumPriorityTickets
        );


        model.addAttribute(
                "lowPriorityTickets",
                lowPriorityTickets
        );


        model.addAttribute(
                "urgentPriorityTickets",
                urgentPriorityTickets
        );


        // =====================================================
        // RESOLVED OVER TIME DATA
        // =====================================================

        model.addAttribute(
                "resolvedMonthLabels",
                resolvedMonthLabels
        );


        model.addAttribute(
                "resolvedMonthValues",
                resolvedMonthValues
        );


        return "my-work-overview";
    }


    // =========================================================
    // CHECK IN
    // =========================================================

    @PostMapping("/check-in")
    public String checkIn(
            Authentication authentication) {

        User technician =
                getLoggedInTechnician(authentication);

        attendanceService.checkIn(
                technician
        );

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

        attendanceService.startLunch(
                technician
        );

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

        attendanceService.endLunch(
                technician
        );

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

        attendanceService.checkOut(
                technician
        );

        return "redirect:/technician-dashboard";
    }


    // =========================================================
    // FIND LOGGED-IN TECHNICIAN
    // =========================================================

    private User getLoggedInTechnician(
            Authentication authentication) {

        if (authentication == null) {

            throw new RuntimeException(
                    "Authentication information is not available."
            );
        }


        String username =
                authentication.getName();


        User technician =
                userService.findByUsername(
                        username
                );


        if (technician == null) {

            throw new RuntimeException(
                    "Logged-in technician not found: "
                            + username
            );
        }


        return technician;
    }
}