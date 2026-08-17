package za.gov.gautengems.itsupport.service;

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

    /*
     * Maximum allowed lunch duration.
     *
     * 90 minutes = 1 hour 30 minutes.
     */
    private static final long MAX_LUNCH_MINUTES = 90;


    public TechnicianAttendanceService(
            TechnicianAttendanceRepository attendanceRepository) {

        this.attendanceRepository = attendanceRepository;
    }


    // =========================================================
    // GET TODAY'S ATTENDANCE
    // =========================================================

    public Optional<TechnicianAttendance> getTodayAttendance(
            User technician) {

        return attendanceRepository
                .findByTechnicianAndWorkDate(
                        technician,
                        LocalDate.now()
                );
    }


    // =========================================================
    // GET ALL TODAY'S ATTENDANCE
    //
    // Used by the ADMIN dashboard.
    // =========================================================

    public List<TechnicianAttendance> getTodayAttendances() {

        return attendanceRepository
                .findByWorkDate(LocalDate.now());
    }


    // =========================================================
    // CHECK IN
    // =========================================================

    public TechnicianAttendance checkIn(
            User technician) {

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
             * If the technician previously checked out,
             * we do not allow another check-in for the
             * same working day.
             */
            if (attendance.getCheckOut() != null) {

                throw new IllegalStateException(
                        "You have already checked out for today."
                );
            }

            return attendance;
        }


        TechnicianAttendance attendance =
                new TechnicianAttendance();


        attendance.setTechnician(technician);

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
         * Technician cannot go to lunch after checking out.
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
         * Clear previous lunch end if necessary.
         */
        attendance.setLunchEnd(null);


        /*
         * Technician becomes OFFLINE from the
         * manager's point of view because they are
         * currently on lunch.
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
         * Record the actual return time.
         *
         * IMPORTANT:
         * Even if the 90-minute limit was exceeded,
         * the technician must still manually click
         * "Return to Work".
         */
        attendance.setLunchEnd(
                LocalDateTime.now()
        );


        /*
         * Technician becomes WORKING again.
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
         * Technician cannot check out twice.
         */
        if (attendance.getCheckOut() != null) {

            throw new IllegalStateException(
                    "Technician has already checked out."
            );
        }


        /*
         * Technician cannot check out while on lunch.
         *
         * They must first return to work.
         */
        if (attendance.getStatus()
                == TechnicianAttendance.AttendanceStatus.LUNCH) {

            throw new IllegalStateException(
                    "Please return from lunch before checking out."
            );
        }


        /*
         * Record actual checkout time.
         */
        attendance.setCheckOut(
                LocalDateTime.now()
        );


        /*
         * Technician becomes offline.
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
    // Used by the technician dashboard countdown.
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


        LocalDateTime lunchEnd =
                attendance.getLunchStart()
                        .plusMinutes(MAX_LUNCH_MINUTES);


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