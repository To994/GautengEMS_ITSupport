package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import za.gov.gautengems.itsupport.entity.Ticket;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.entity.TechnicianAttendance;
import za.gov.gautengems.itsupport.service.TicketService;
import za.gov.gautengems.itsupport.service.UserService;
import za.gov.gautengems.itsupport.service.TechnicianAttendanceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    private final TicketService ticketService;
    private final UserService userService;
    private final TechnicianAttendanceService attendanceService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AuthController(
            TicketService ticketService,
            UserService userService,
            TechnicianAttendanceService attendanceService) {

        this.ticketService = ticketService;
        this.userService = userService;
        this.attendanceService = attendanceService;
    }


    // =========================================================
    // LOGIN PAGE
    // =========================================================

    @GetMapping("/login")
    public String login() {

        return "login";
    }


    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {


        // =====================================================
        // GET ALL TICKETS
        // =====================================================

        List<Ticket> allTickets =
                ticketService.getAllTickets();


        // =====================================================
        // TICKET STATUS COUNTS
        // =====================================================

        long openTickets =
                ticketService.countByStatus("OPEN");

        long pendingTickets =
                ticketService.countByStatus("PENDING");

        long inProgressTickets =
                ticketService.countByStatus("IN_PROGRESS");

        long resolvedTickets =
                ticketService.countByStatus("RESOLVED");


        // =====================================================
        // ACTIVE / UNRESOLVED TICKETS
        // =====================================================

        long activeTickets =
                openTickets
                        + pendingTickets
                        + inProgressTickets;


        // =====================================================
        // ACTIVE USERS
        // =====================================================

        long activeUsers =
                userService.getAllUsers()
                        .stream()
                        .filter(User::isActive)
                        .count();


        // =====================================================
        // RECENT TICKETS
        // =====================================================

        List<Ticket> recentTickets =
                new ArrayList<>(
                        allTickets.stream()
                                .skip(
                                        Math.max(
                                                0,
                                                allTickets.size() - 5
                                        )
                                )
                                .toList()
                );

        Collections.reverse(recentTickets);


        // =====================================================
        // LOGGED-IN USER
        // =====================================================

        String username =
                authentication != null
                        ? authentication.getName()
                        : "Administrator";


        // =====================================================
        // TECHNICIAN ATTENDANCE
        // =====================================================

        List<Map<String, Object>> technicianAttendance =
                new ArrayList<>();


        List<User> technicians =
                userService.getAllUsers()
                        .stream()
                        .filter(user ->
                                user.getRole()
                                        == User.Role.TECHNICIAN)
                        .toList();


        long onlineTechnicians = 0;
        long lunchTechnicians = 0;
        long offlineTechnicians = 0;
        long overdueLunchTechnicians = 0;


        for (User technician : technicians) {

            Map<String, Object> row =
                    new HashMap<>();


            // -------------------------------------------------
            // TECHNICIAN
            // -------------------------------------------------

            row.put(
                    "technician",
                    technician
            );


            // -------------------------------------------------
            // TODAY'S ATTENDANCE
            // -------------------------------------------------

            TechnicianAttendance attendance =
                    attendanceService
                            .getTodayAttendance(technician)
                            .orElse(null);


            row.put(
                    "attendance",
                    attendance
            );


            // -------------------------------------------------
            // STATUS
            // -------------------------------------------------

            String status = "OFFLINE";


            if (attendance != null) {

                if (attendance.getStatus()
                        == TechnicianAttendance.AttendanceStatus.WORKING) {

                    status = "ONLINE";

                    onlineTechnicians++;

                }

                else if (attendance.getStatus()
                        == TechnicianAttendance.AttendanceStatus.LUNCH) {

                    status = "LUNCH";

                    lunchTechnicians++;


                    // -------------------------------------------------
                    // CHECK WHETHER LUNCH EXCEEDED 90 MINUTES
                    // -------------------------------------------------

                    if (attendance.getLunchStart() != null) {

                        java.time.Duration lunchDuration =
                                java.time.Duration.between(
                                        attendance.getLunchStart(),
                                        java.time.LocalDateTime.now()
                                );


                        if (lunchDuration.toMinutes() >= 90) {

                            status = "LUNCH_EXCEEDED";

                            overdueLunchTechnicians++;

                        }

                    }

                }

                else {

                    status = "OFFLINE";

                    offlineTechnicians++;

                }

            }

            else {

                offlineTechnicians++;

            }


            row.put(
                    "status",
                    status
            );


            technicianAttendance.add(row);
        }


        // =====================================================
        // SEND TECHNICIAN ATTENDANCE TO DASHBOARD
        // =====================================================

        model.addAttribute(
                "technicianAttendance",
                technicianAttendance
        );


        model.addAttribute(
                "onlineTechnicians",
                onlineTechnicians
        );


        model.addAttribute(
                "lunchTechnicians",
                lunchTechnicians
        );


        model.addAttribute(
                "offlineTechnicians",
                offlineTechnicians
        );


        model.addAttribute(
                "overdueLunchTechnicians",
                overdueLunchTechnicians
        );


        // =====================================================
        // SEND EXISTING DASHBOARD DATA
        // =====================================================

        model.addAttribute(
                "username",
                username
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
                "activeTickets",
                activeTickets
        );

        model.addAttribute(
                "activeUsers",
                activeUsers
        );

        model.addAttribute(
                "recentTickets",
                recentTickets
        );


        // =====================================================
        // OPEN DASHBOARD
        // =====================================================

        return "dashboard";
    }


    // =========================================================
    // LIVE DASHBOARD STATISTICS
    // =========================================================

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public Map<String, Long> dashboardStats() {


        long openTickets =
                ticketService.countByStatus("OPEN");


        long pendingTickets =
                ticketService.countByStatus("PENDING");


        long inProgressTickets =
                ticketService.countByStatus("IN_PROGRESS");


        long resolvedTickets =
                ticketService.countByStatus("RESOLVED");


        long activeTickets =
                openTickets
                        + pendingTickets
                        + inProgressTickets;


        long activeUsers =
                userService.getAllUsers()
                        .stream()
                        .filter(User::isActive)
                        .count();


        Map<String, Long> stats =
                new HashMap<>();


        stats.put(
                "openTickets",
                openTickets
        );

        stats.put(
                "pendingTickets",
                pendingTickets
        );

        stats.put(
                "inProgressTickets",
                inProgressTickets
        );

        stats.put(
                "resolvedTickets",
                resolvedTickets
        );

        stats.put(
                "activeTickets",
                activeTickets
        );

        stats.put(
                "activeUsers",
                activeUsers
        );


        return stats;
    }
}