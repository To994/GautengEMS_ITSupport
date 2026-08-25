package za.gov.gautengems.itsupport.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import za.gov.gautengems.itsupport.entity.TechnicianAttendance;
import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.repository.TechnicianAttendanceRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TechnicianAttendanceService {

    private final TechnicianAttendanceRepository attendanceRepository;
    private final UserService userService;

    /*
     * Maximum allowed lunch duration.
     *
     * 90 minutes = 1 hour 30 minutes.
     */
    private static final long MAX_LUNCH_MINUTES = 90;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TechnicianAttendanceService(
            TechnicianAttendanceRepository attendanceRepository,
            UserService userService) {

        this.attendanceRepository = attendanceRepository;
        this.userService = userService;
    }


    // =========================================================
    // GET LOGGED-IN TECHNICIAN
    // =========================================================

    public User getLoggedInTechnician(
            Authentication authentication) {

        if (authentication == null) {

            throw new IllegalStateException(
                    "Authentication information is not available."
            );
        }

        String username =
                authentication.getName();

        User technician =
                userService.findByUsername(username);

        /*
         * IMPORTANT:
         *
         * findByUsername() returns User directly,
         * NOT Optional<User>.
         *
         * Therefore we DO NOT use .orElseThrow().
         */
        if (technician == null) {

            throw new IllegalStateException(
                    "Logged-in technician not found: "
                            + username
            );
        }

        return technician;
    }


    // =========================================================
    // GET TODAY'S ATTENDANCE
    // =========================================================

    public Optional<TechnicianAttendance> getTodayAttendance(
            User technician) {

        if (technician == null) {

            return Optional.empty();
        }

        return attendanceRepository
                .findByTechnicianAndWorkDate(
                        technician,
                        LocalDate.now()
                );
    }


    // =========================================================
    // GET ALL TODAY'S ATTENDANCE
    //
    // Used by ADMIN dashboard.
    // =========================================================

    public List<TechnicianAttendance> getTodayAttendances() {

        return attendanceRepository
                .findByWorkDate(
                        LocalDate.now()
                );
    }


    // =========================================================
    // CHECK IN
    // =========================================================

    public TechnicianAttendance checkIn(
            User technician) {

        if (technician == null) {

            throw new IllegalArgumentException(
                    "Technician cannot be null."
            );
        }

        Optional<TechnicianAttendance> existing =
                getTodayAttendance(technician);


        /*
         * If today's attendance already exists,
         * don't create another record.
         */
        if (existing.isPresent()) {

            TechnicianAttendance attendance =
                    existing.get();


            /*
             * If the technician already checked out,
             * another check-in is not allowed.
             */
            if (attendance.getCheckOut() != null) {

                throw new IllegalStateException(
                        "You have already checked out for today."
                );
            }


            return attendance;
        }


        /*
         * Create new attendance record.
         */
        TechnicianAttendance attendance =
                new TechnicianAttendance();


        attendance.setTechnician(
                technician
        );

        attendance.setWorkDate(
                LocalDate.now()
        );

        attendance.setCheckIn(
                LocalDateTime.now()
        );

        attendance.setStatus(
                TechnicianAttendance.AttendanceStatus.WORKING
        );


        return attendanceRepository.save(
                attendance
        );
    }


    // =========================================================
    // START LUNCH
    // =========================================================

    public TechnicianAttendance startLunch(
            User technician) {

        TechnicianAttendance attendance =
                getTodayAttendance(technician)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Technician has not checked in today."
                                )
                        );


        /*
         * Cannot go to lunch after checking out.
         */
        if (attendance.getCheckOut() != null) {

            throw new IllegalStateException(
                    "Technician has already checked out."
            );
        }


        /*
         * Technician must currently be working.
         */
        if (attendance.getStatus()
                != TechnicianAttendance.AttendanceStatus.WORKING) {

            throw new IllegalStateException(
                    "Technician is not currently working."
            );
        }


        /*
         * Record lunch start time.
         */
        attendance.setLunchStart(
                LocalDateTime.now()
        );


        /*
         * Clear previous lunch end time.
         */
        attendance.setLunchEnd(
                null
        );


        /*
         * Change status to LUNCH.
         */
        attendance.setStatus(
                TechnicianAttendance.AttendanceStatus.LUNCH
        );


        return attendanceRepository.save(
                attendance
        );
    }


    // =========================================================
    // RETURN FROM LUNCH
    // =========================================================

    public TechnicianAttendance endLunch(
            User technician) {

        TechnicianAttendance attendance =
                getTodayAttendance(technician)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Technician has not checked in today."
                                )
                        );


        /*
         * Technician must currently be on lunch.
         */
        if (attendance.getStatus()
                != TechnicianAttendance.AttendanceStatus.LUNCH) {

            throw new IllegalStateException(
                    "Technician is not currently on lunch."
            );
        }


        /*
         * Record lunch return time.
         */
        attendance.setLunchEnd(
                LocalDateTime.now()
        );


        /*
         * Technician is working again.
         */
        attendance.setStatus(
                TechnicianAttendance.AttendanceStatus.WORKING
        );


        return attendanceRepository.save(
                attendance
        );
    }


    // =========================================================
    // CHECK OUT
    // =========================================================

    public TechnicianAttendance checkOut(
            User technician) {

        TechnicianAttendance attendance =
                getTodayAttendance(technician)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Technician has not checked in today."
                                )
                        );


        /*
         * Cannot check out twice.
         */
        if (attendance.getCheckOut() != null) {

            throw new IllegalStateException(
                    "Technician has already checked out."
            );
        }


        /*
         * Technician must return from lunch first.
         */
        if (attendance.getStatus()
                == TechnicianAttendance.AttendanceStatus.LUNCH) {

            throw new IllegalStateException(
                    "Please return from lunch before checking out."
            );
        }


        /*
         * Record checkout time.
         */
        attendance.setCheckOut(
                LocalDateTime.now()
        );


        /*
         * Change status to CHECKED_OUT.
         */
        attendance.setStatus(
                TechnicianAttendance.AttendanceStatus.CHECKED_OUT
        );


        return attendanceRepository.save(
                attendance
        );
    }


    // =========================================================
    // CHECK IF LUNCH LIMIT HAS BEEN EXCEEDED
    // =========================================================

    public boolean isLunchExceeded(
            TechnicianAttendance attendance) {

        /*
         * No attendance record.
         */
        if (attendance == null) {

            return false;
        }


        /*
         * Technician is not currently on lunch.
         */
        if (attendance.getStatus()
                != TechnicianAttendance.AttendanceStatus.LUNCH) {

            return false;
        }


        /*
         * No lunch start time.
         */
        if (attendance.getLunchStart() == null) {

            return false;
        }


        long minutes =
                Duration.between(
                        attendance.getLunchStart(),
                        LocalDateTime.now()
                ).toMinutes();


        return minutes >= MAX_LUNCH_MINUTES;
    }


    // =========================================================
    // GET REMAINING LUNCH SECONDS
    //
    // Used by technician dashboard countdown.
    // =========================================================

    public long getRemainingLunchSeconds(
            TechnicianAttendance attendance) {

        /*
         * Not currently on lunch.
         */
        if (attendance == null
                || attendance.getLunchStart() == null
                || attendance.getStatus()
                != TechnicianAttendance.AttendanceStatus.LUNCH) {

            return 0;
        }


        /*
         * Calculate lunch expiry time.
         */
        LocalDateTime lunchEnd =
                attendance.getLunchStart()
                        .plusMinutes(
                                MAX_LUNCH_MINUTES
                        );


        long seconds =
                Duration.between(
                        LocalDateTime.now(),
                        lunchEnd
                ).getSeconds();


        /*
         * Never return a negative countdown.
         */
        return Math.max(
                0,
                seconds
        );
    }


    // =========================================================
    // GET MAXIMUM LUNCH MINUTES
    // =========================================================

    public long getMaximumLunchMinutes() {

        return MAX_LUNCH_MINUTES;
    }
}