package za.gov.gautengems.itsupport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.gov.gautengems.itsupport.entity.Ticket;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // =========================================================
    // FIND TICKETS BY STATUS
    // =========================================================

    List<Ticket> findByStatus(String status);


    // =========================================================
    // FIND TICKETS BY PRIORITY
    // =========================================================

    List<Ticket> findByPriority(String priority);


    // =========================================================
    // FIND TICKETS BY TECHNICIAN
    // =========================================================

    List<Ticket> findByAssignedTechnician(
            String assignedTechnician
    );


    // =========================================================
    // FIND TICKETS BY REQUESTER EMAIL
    // =========================================================

    List<Ticket> findByRequesterEmail(
            String requesterEmail
    );


    // =========================================================
    // COUNT TICKETS BY STATUS
    // =========================================================

    long countByStatus(String status);


    // =========================================================
    // COUNT TICKETS BY TECHNICIAN
    // =========================================================

    long countByAssignedTechnician(
            String assignedTechnician
    );


    // =========================================================
    // COUNT TECHNICIAN TICKETS BY STATUS
    // =========================================================

    long countByAssignedTechnicianAndStatus(
            String assignedTechnician,
            String status
    );
}