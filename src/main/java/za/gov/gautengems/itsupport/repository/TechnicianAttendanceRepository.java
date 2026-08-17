package za.gov.gautengems.itsupport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.gov.gautengems.itsupport.entity.TechnicianAttendance;
import za.gov.gautengems.itsupport.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TechnicianAttendanceRepository
        extends JpaRepository<TechnicianAttendance, Long> {


    // =========================================================
    // GET TODAY'S ATTENDANCE FOR ONE TECHNICIAN
    // =========================================================

    Optional<TechnicianAttendance> findByTechnicianAndWorkDate(
            User technician,
            LocalDate workDate
    );


    // =========================================================
    // GET TODAY'S ATTENDANCE FOR ALL TECHNICIANS
    // =========================================================

    List<TechnicianAttendance> findByWorkDate(
            LocalDate workDate
    );
}