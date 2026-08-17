package za.gov.gautengems.itsupport.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "technician_attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"technician_id", "work_date"}
                )
        }
)
public class TechnicianAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // TECHNICIAN
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;


    // =========================================================
    // WORK DATE
    // =========================================================

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;


    // =========================================================
    // ATTENDANCE TIMES
    // =========================================================

    private LocalDateTime checkIn;

    private LocalDateTime lunchStart;

    private LocalDateTime lunchEnd;

    private LocalDateTime checkOut;


    // =========================================================
    // LUNCH LIMIT
    // =========================================================

    /*
     * The exact time at which the technician's
     * 90-minute lunch allowance expires.
     *
     * Example:
     *
     * Lunch starts: 12:00
     * Lunch expires: 13:30
     */

    private LocalDateTime lunchAllowedUntil;


    // =========================================================
    // CURRENT STATUS
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status =
            AttendanceStatus.CHECKED_OUT;


    // =========================================================
    // STATUS OPTIONS
    // =========================================================

    public enum AttendanceStatus {

        /*
         * Technician has checked out.
         * OFFLINE.
         */
        CHECKED_OUT,


        /*
         * Technician has checked in
         * and is currently working.
         * ONLINE.
         */
        WORKING,


        /*
         * Technician is currently on lunch
         * and has not exceeded 90 minutes.
         * OFFLINE.
         */
        LUNCH,


        /*
         * Technician's 90-minute lunch allowance
         * has expired, but they have NOT returned.
         *
         * OFFLINE.
         */
        LUNCH_EXCEEDED,


        /*
         * Existing status.
         *
         * We are keeping this so that we do not
         * break any existing code that may already
         * use AWAY.
         */
        AWAY
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TechnicianAttendance() {
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }


    public User getTechnician() {
        return technician;
    }


    public void setTechnician(User technician) {
        this.technician = technician;
    }


    public LocalDate getWorkDate() {
        return workDate;
    }


    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }


    public LocalDateTime getCheckIn() {
        return checkIn;
    }


    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }


    public LocalDateTime getLunchStart() {
        return lunchStart;
    }


    public void setLunchStart(LocalDateTime lunchStart) {
        this.lunchStart = lunchStart;
    }


    public LocalDateTime getLunchEnd() {
        return lunchEnd;
    }


    public void setLunchEnd(LocalDateTime lunchEnd) {
        this.lunchEnd = lunchEnd;
    }


    public LocalDateTime getCheckOut() {
        return checkOut;
    }


    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }


    // =========================================================
    // LUNCH ALLOWED UNTIL
    // =========================================================

    public LocalDateTime getLunchAllowedUntil() {
        return lunchAllowedUntil;
    }


    public void setLunchAllowedUntil(
            LocalDateTime lunchAllowedUntil) {

        this.lunchAllowedUntil = lunchAllowedUntil;
    }


    // =========================================================
    // STATUS
    // =========================================================

    public AttendanceStatus getStatus() {
        return status;
    }


    public void setStatus(
            AttendanceStatus status) {

        this.status = status;
    }
}