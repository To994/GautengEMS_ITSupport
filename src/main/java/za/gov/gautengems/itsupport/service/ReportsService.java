package za.gov.gautengems.itsupport.service;

import org.springframework.stereotype.Service;

import za.gov.gautengems.itsupport.entity.Ticket;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    private final TicketService ticketService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReportsService(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    // =========================================================
    // GET ALL TICKETS
    // =========================================================

    private List<Ticket> getTickets() {

        return ticketService.getAllTickets();
    }


    // =========================================================
    // TOTAL TICKETS
    // =========================================================

    public long getTotalTickets() {

        return getTickets().size();
    }


    // =========================================================
    // COUNT BY STATUS
    // =========================================================

    public long countByStatus(String status) {

        return getTickets()
                .stream()
                .filter(ticket ->
                        ticket.getStatus() != null
                                && ticket.getStatus()
                                .equalsIgnoreCase(status))
                .count();
    }


    // =========================================================
    // STATUS REPORT
    // =========================================================

    public Map<String, Long> getStatusReport() {

        Map<String, Long> report =
                new LinkedHashMap<>();

        report.put(
                "OPEN",
                countByStatus("OPEN")
        );

        report.put(
                "PENDING",
                countByStatus("PENDING")
        );

        report.put(
                "IN_PROGRESS",
                countByStatus("IN_PROGRESS")
        );

        report.put(
                "RESOLVED",
                countByStatus("RESOLVED")
        );

        report.put(
                "CLOSED",
                countByStatus("CLOSED")
        );

        return report;
    }


    // =========================================================
    // CATEGORY REPORT
    // =========================================================

    public Map<String, Long> getCategoryReport() {

        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getCategory() != null
                                        && !ticket.getCategory().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getCategory,
                                        Collectors.counting()
                                )
                        );

        return sortDescending(report);
    }


    // =========================================================
    // PRIORITY REPORT
    // =========================================================

    public Map<String, Long> getPriorityReport() {

        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getPriority() != null
                                        && !ticket.getPriority().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getPriority,
                                        Collectors.counting()
                                )
                        );

        return sortDescending(report);
    }


    // =========================================================
    // DEPARTMENT REPORT
    // =========================================================

    public Map<String, Long> getDepartmentReport() {

        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getDepartment() != null
                                        && !ticket.getDepartment().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getDepartment,
                                        Collectors.counting()
                                )
                        );

        return sortDescending(report);
    }


    // =========================================================
    // DISTRICT REPORT
    // =========================================================

    public Map<String, Long> getDistrictReport() {

        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getDistrict() != null
                                        && !ticket.getDistrict().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getDistrict,
                                        Collectors.counting()
                                )
                        );

        return sortDescending(report);
    }


    // =========================================================
    // STATION / UNIT REPORT
    // =========================================================

    public Map<String, Long> getStationReport() {

        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getStationUnit() != null
                                        && !ticket.getStationUnit().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getStationUnit,
                                        Collectors.counting()
                                )
                        );

        return sortDescending(report);
    }


    // =========================================================
    // PROBLEM TYPE REPORT
    // =========================================================

    public Map<String, Long> getProblemTypeReport() {

        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getProblemType() != null
                                        && !ticket.getProblemType().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getProblemType,
                                        Collectors.counting()
                                )
                        );

        return sortDescending(report);
    }


    // =========================================================
    // TECHNICIAN WORKLOAD
    // =========================================================

    public Map<String, Long> getTechnicianWorkload() {

        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getAssignedTechnician() != null
                                        && !ticket.getAssignedTechnician().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getAssignedTechnician,
                                        Collectors.counting()
                                )
                        );

        return sortDescending(report);
    }


    // =========================================================
    // TECHNICIAN RESOLVED TICKETS
    // =========================================================

    public Map<String, Long> getTechnicianResolvedTickets() {

        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getAssignedTechnician() != null
                                        && !ticket.getAssignedTechnician().isBlank()
                                        && ticket.getStatus() != null
                                        && ticket.getStatus()
                                        .equalsIgnoreCase("RESOLVED"))
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getAssignedTechnician,
                                        Collectors.counting()
                                )
                        );

        return sortDescending(report);
    }


    // =========================================================
    // CURRENT WEEK DAILY REPORT
    // =========================================================

    public Map<String, Long> getCurrentWeekReport() {

        LocalDate today =
                LocalDate.now();

        LocalDate monday =
                today.with(
                        TemporalAdjusters.previousOrSame(
                                DayOfWeek.MONDAY
                        )
                );

        LocalDate sunday =
                monday.plusDays(6);


        Map<String, Long> report =
                new LinkedHashMap<>();


        for (int i = 0; i < 7; i++) {

            LocalDate date =
                    monday.plusDays(i);

            long count =
                    getTickets()
                            .stream()
                            .filter(ticket ->
                                    isCreatedOnDate(
                                            ticket,
                                            date
                                    ))
                            .count();


            String dayName =
                    date.getDayOfWeek()
                            .toString()
                            .substring(0, 3);


            report.put(
                    dayName,
                    count
            );
        }


        return report;
    }


    // =========================================================
    // LAST 7 DAYS REPORT
    // =========================================================

    public Map<String, Long> getLast7DaysReport() {

        LocalDate today =
                LocalDate.now();

        Map<String, Long> report =
                new LinkedHashMap<>();


        for (int i = 6; i >= 0; i--) {

            LocalDate date =
                    today.minusDays(i);


            long count =
                    getTickets()
                            .stream()
                            .filter(ticket ->
                                    isCreatedOnDate(
                                            ticket,
                                            date
                                    ))
                            .count();


            String label =
                    date.getDayOfMonth()
                            + " "
                            + date.getMonth()
                            .toString()
                            .substring(0, 3);


            report.put(
                    label,
                    count
            );
        }


        return report;
    }


    // =========================================================
    // CURRENT MONTH DAILY REPORT
    // =========================================================

    public Map<String, Long> getCurrentMonthDailyReport() {

        YearMonth currentMonth =
                YearMonth.now();

        LocalDate firstDay =
                currentMonth.atDay(1);

        LocalDate lastDay =
                currentMonth.atEndOfMonth();


        Map<String, Long> report =
                new LinkedHashMap<>();


        LocalDate currentDate =
                firstDay;


        while (!currentDate.isAfter(lastDay)) {

            final LocalDate date =
                    currentDate;


            long count =
                    getTickets()
                            .stream()
                            .filter(ticket ->
                                    isCreatedOnDate(
                                            ticket,
                                            date
                                    ))
                            .count();


            report.put(
                    String.valueOf(
                            date.getDayOfMonth()
                    ),
                    count
            );


            currentDate =
                    currentDate.plusDays(1);
        }


        return report;
    }


    // =========================================================
    // MONTHLY TICKET COUNT
    // =========================================================

    public long getTicketCountForMonth(
            YearMonth month) {

        LocalDateTime start =
                month.atDay(1)
                        .atStartOfDay();

        LocalDateTime end =
                month.atEndOfMonth()
                        .plusDays(1)
                        .atStartOfDay();


        return getTickets()
                .stream()
                .filter(ticket ->
                        ticket.getCreatedAt() != null
                                && !ticket.getCreatedAt()
                                .isBefore(start)
                                && ticket.getCreatedAt()
                                .isBefore(end))
                .count();
    }


    // =========================================================
    // CURRENT MONTH
    // =========================================================

    public long getCurrentMonthTickets() {

        return getTicketCountForMonth(
                YearMonth.now()
        );
    }


    // =========================================================
    // PREVIOUS MONTH
    // =========================================================

    public long getPreviousMonthTickets() {

        return getTicketCountForMonth(
                YearMonth.now().minusMonths(1)
        );
    }


    // =========================================================
    // MONTHLY COMPARISON
    // =========================================================

    public Map<String, Long> getMonthlyComparison() {

        Map<String, Long> report =
                new LinkedHashMap<>();


        YearMonth current =
                YearMonth.now();


        for (int i = 5; i >= 0; i--) {

            YearMonth month =
                    current.minusMonths(i);


            String label =
                    month.getMonth()
                            .toString()
                            .substring(0, 3)
                            + " "
                            + month.getYear();


            report.put(
                    label,
                    getTicketCountForMonth(month)
            );
        }


        return report;
    }


    // =========================================================
    // MONTHLY CHANGE PERCENTAGE
    // =========================================================

    public double getMonthlyChangePercentage() {

        long current =
                getCurrentMonthTickets();

        long previous =
                getPreviousMonthTickets();


        if (previous == 0) {

            if (current == 0) {
                return 0.0;
            }

            return 100.0;
        }


        return (
                ((double) current - previous)
                        / previous
        ) * 100.0;
    }


    // =========================================================
    // CURRENT MONTH TOP CATEGORY
    // =========================================================

    public String getTopCategoryThisMonth() {

        YearMonth currentMonth =
                YearMonth.now();


        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getCreatedAt() != null
                                        && YearMonth.from(
                                        ticket.getCreatedAt()
                                ).equals(currentMonth))
                        .filter(ticket ->
                                ticket.getCategory() != null
                                        && !ticket.getCategory().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getCategory,
                                        Collectors.counting()
                                )
                        );


        return report.entrySet()
                .stream()
                .max(
                        Map.Entry.comparingByValue()
                )
                .map(Map.Entry::getKey)
                .orElse("No data");
    }


    // =========================================================
    // CURRENT MONTH TOP DEPARTMENT
    // =========================================================

    public String getTopDepartmentThisMonth() {

        YearMonth currentMonth =
                YearMonth.now();


        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getCreatedAt() != null
                                        && YearMonth.from(
                                        ticket.getCreatedAt()
                                ).equals(currentMonth))
                        .filter(ticket ->
                                ticket.getDepartment() != null
                                        && !ticket.getDepartment().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getDepartment,
                                        Collectors.counting()
                                )
                        );


        return report.entrySet()
                .stream()
                .max(
                        Map.Entry.comparingByValue()
                )
                .map(Map.Entry::getKey)
                .orElse("No data");
    }


    // =========================================================
    // CURRENT MONTH TOP STATION
    // =========================================================

    public String getTopStationThisMonth() {

        YearMonth currentMonth =
                YearMonth.now();


        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getCreatedAt() != null
                                        && YearMonth.from(
                                        ticket.getCreatedAt()
                                ).equals(currentMonth))
                        .filter(ticket ->
                                ticket.getStationUnit() != null
                                        && !ticket.getStationUnit().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getStationUnit,
                                        Collectors.counting()
                                )
                        );


        return report.entrySet()
                .stream()
                .max(
                        Map.Entry.comparingByValue()
                )
                .map(Map.Entry::getKey)
                .orElse("No data");
    }


    // =========================================================
    // TOP PRIORITY THIS MONTH
    // =========================================================

    public String getTopPriorityThisMonth() {

        YearMonth currentMonth =
                YearMonth.now();


        Map<String, Long> report =
                getTickets()
                        .stream()
                        .filter(ticket ->
                                ticket.getCreatedAt() != null
                                        && YearMonth.from(
                                        ticket.getCreatedAt()
                                ).equals(currentMonth))
                        .filter(ticket ->
                                ticket.getPriority() != null
                                        && !ticket.getPriority().isBlank())
                        .collect(
                                Collectors.groupingBy(
                                        Ticket::getPriority,
                                        Collectors.counting()
                                )
                        );


        return report.entrySet()
                .stream()
                .max(
                        Map.Entry.comparingByValue()
                )
                .map(Map.Entry::getKey)
                .orElse("No data");
    }


    // =========================================================
    // SORT MAP BY VALUE DESCENDING
    // =========================================================

    private Map<String, Long> sortDescending(
            Map<String, Long> input) {

        return input.entrySet()
                .stream()
                .sorted(
                        Map.Entry
                                .<String, Long>comparingByValue()
                                .reversed()
                )
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (existing, replacement) ->
                                        existing,
                                LinkedHashMap::new
                        )
                );
    }


    // =========================================================
    // CHECK CREATED DATE
    // =========================================================

    private boolean isCreatedOnDate(
            Ticket ticket,
            LocalDate date) {

        if (ticket == null
                || ticket.getCreatedAt() == null
                || date == null) {

            return false;
        }


        return ticket.getCreatedAt()
                .toLocalDate()
                .equals(date);
    }
}