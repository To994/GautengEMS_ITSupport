package za.gov.gautengems.itsupport.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import za.gov.gautengems.itsupport.service.ReportsService;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final ReportsService reportsService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReportsController(
            ReportsService reportsService) {

        this.reportsService = reportsService;
    }


    // =========================================================
    // REPORTS DASHBOARD
    // =========================================================

    @GetMapping
    public String reports(
            Authentication authentication,
            Model model) {


        // =====================================================
        // SECURITY CHECK
        // =====================================================

        if (authentication == null
                || !authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"))) {

            return "redirect:/login?error=true";
        }


        // =====================================================
        // HEADER INFORMATION
        // =====================================================

        model.addAttribute(
                "username",
                authentication.getName()
        );


        // =====================================================
        // GENERAL STATISTICS
        // =====================================================

        model.addAttribute(
                "totalTickets",
                reportsService.getTotalTickets()
        );


        // =====================================================
        // STATUS REPORT
        // =====================================================

        model.addAttribute(
                "statusReport",
                reportsService.getStatusReport()
        );


        // =====================================================
        // CATEGORY REPORT
        // =====================================================

        model.addAttribute(
                "categoryReport",
                reportsService.getCategoryReport()
        );


        // =====================================================
        // PRIORITY REPORT
        // =====================================================

        model.addAttribute(
                "priorityReport",
                reportsService.getPriorityReport()
        );


        // =====================================================
        // DEPARTMENT REPORT
        // =====================================================

        model.addAttribute(
                "departmentReport",
                reportsService.getDepartmentReport()
        );


        // =====================================================
        // DISTRICT REPORT
        // =====================================================

        model.addAttribute(
                "districtReport",
                reportsService.getDistrictReport()
        );


        // =====================================================
        // STATION REPORT
        // =====================================================

        model.addAttribute(
                "stationReport",
                reportsService.getStationReport()
        );


        // =====================================================
        // PROBLEM TYPE REPORT
        // =====================================================

        model.addAttribute(
                "problemTypeReport",
                reportsService.getProblemTypeReport()
        );


        // =====================================================
        // TECHNICIAN WORKLOAD
        // =====================================================

        model.addAttribute(
                "technicianWorkload",
                reportsService.getTechnicianWorkload()
        );


        // =====================================================
        // TECHNICIAN RESOLVED TICKETS
        // =====================================================

        model.addAttribute(
                "technicianResolvedTickets",
                reportsService.getTechnicianResolvedTickets()
        );


        // =====================================================
        // CURRENT WEEK
        // =====================================================

        model.addAttribute(
                "currentWeekReport",
                reportsService.getCurrentWeekReport()
        );


        // =====================================================
        // LAST 7 DAYS
        // =====================================================

        model.addAttribute(
                "last7DaysReport",
                reportsService.getLast7DaysReport()
        );


        // =====================================================
        // CURRENT MONTH DAILY
        // =====================================================

        model.addAttribute(
                "currentMonthDailyReport",
                reportsService.getCurrentMonthDailyReport()
        );


        // =====================================================
        // MONTHLY COMPARISON
        // =====================================================

        model.addAttribute(
                "monthlyComparison",
                reportsService.getMonthlyComparison()
        );


        // =====================================================
        // CURRENT MONTH
        // =====================================================

        model.addAttribute(
                "currentMonthTickets",
                reportsService.getCurrentMonthTickets()
        );


        // =====================================================
        // PREVIOUS MONTH
        // =====================================================

        model.addAttribute(
                "previousMonthTickets",
                reportsService.getPreviousMonthTickets()
        );


        // =====================================================
        // MONTHLY CHANGE
        // =====================================================

        model.addAttribute(
                "monthlyChangePercentage",
                reportsService.getMonthlyChangePercentage()
        );


        // =====================================================
        // TOP CATEGORY
        // =====================================================

        model.addAttribute(
                "topCategoryThisMonth",
                reportsService.getTopCategoryThisMonth()
        );


        // =====================================================
        // TOP DEPARTMENT
        // =====================================================

        model.addAttribute(
                "topDepartmentThisMonth",
                reportsService.getTopDepartmentThisMonth()
        );


        // =====================================================
        // TOP STATION
        // =====================================================

        model.addAttribute(
                "topStationThisMonth",
                reportsService.getTopStationThisMonth()
        );


        // =====================================================
        // TOP PRIORITY
        // =====================================================

        model.addAttribute(
                "topPriorityThisMonth",
                reportsService.getTopPriorityThisMonth()
        );


        // =====================================================
        // OPEN REPORTS PAGE
        // =====================================================

        return "reports";
    }
}