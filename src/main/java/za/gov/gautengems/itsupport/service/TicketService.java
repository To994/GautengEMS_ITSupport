package za.gov.gautengems.itsupport.service;

import org.springframework.stereotype.Service;

import za.gov.gautengems.itsupport.entity.Ticket;
import za.gov.gautengems.itsupport.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TicketService(TicketRepository ticketRepository) {

        this.ticketRepository = ticketRepository;
    }


    // =========================================================
    // GET ALL TICKETS
    // =========================================================

    public List<Ticket> getAllTickets() {

        return ticketRepository.findAll();
    }


    // =========================================================
    // GET ONE TICKET
    // =========================================================

    public Optional<Ticket> getTicketById(Long id) {

        return ticketRepository.findById(id);
    }


    // =========================================================
    // CREATE OR UPDATE TICKET
    // =========================================================

    public Ticket saveTicket(Ticket ticket) {

        return ticketRepository.save(ticket);
    }


    // =========================================================
    // DELETE TICKET
    // =========================================================

    public void deleteTicket(Long id) {

        ticketRepository.deleteById(id);
    }


    // =========================================================
    // GET TICKETS BY STATUS
    // =========================================================

    public List<Ticket> getTicketsByStatus(String status) {

        return ticketRepository.findByStatus(status);
    }


    // =========================================================
    // GET TICKETS BY PRIORITY
    // =========================================================

    public List<Ticket> getTicketsByPriority(String priority) {

        return ticketRepository.findByPriority(priority);
    }


    // =========================================================
    // GET TICKETS BY TECHNICIAN
    // =========================================================

    public List<Ticket> getTicketsByTechnician(
            String technician) {

        return ticketRepository
                .findByAssignedTechnician(technician);
    }


    // =========================================================
    // GET EMPLOYEE TICKETS
    // =========================================================

    public List<Ticket> getTicketsByRequesterEmail(
            String requesterEmail) {

        return ticketRepository
                .findByRequesterEmail(requesterEmail);
    }


    // =========================================================
    // COUNT TICKETS BY STATUS
    // =========================================================

    public long countByStatus(String status) {

        return ticketRepository.countByStatus(status);
    }


    // =========================================================
    // COUNT TECHNICIAN TICKETS
    // =========================================================

    public long countByTechnician(
            String technician) {

        return ticketRepository
                .countByAssignedTechnician(technician);
    }


    // =========================================================
    // COUNT TECHNICIAN TICKETS BY STATUS
    // =========================================================

    public long countByTechnicianAndStatus(
            String technician,
            String status) {

        return ticketRepository
                .countByAssignedTechnicianAndStatus(
                        technician,
                        status
                );
    }


    // =========================================================
    // ASSIGN TICKET
    // ADMIN ASSIGNS TECHNICIAN
    // =========================================================

    public Ticket assignTicket(
            Long ticketId,
            String technicianName) {

        Ticket ticket = ticketRepository
                .findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found"
                        ));


        // Assign technician

        ticket.setAssignedTechnician(
                technicianName
        );


        // Change status to IN_PROGRESS

        if (!"RESOLVED".equalsIgnoreCase(
                ticket.getStatus())) {

            ticket.setStatus("IN_PROGRESS");
        }


        return ticketRepository.save(ticket);
    }


    // =========================================================
    // TAKE TICKET
    // TECHNICIAN TAKES UNASSIGNED TICKET
    // =========================================================

    public Ticket takeTicket(
            Long ticketId,
            String technicianName) {

        Ticket ticket = ticketRepository
                .findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found"
                        ));


        // -----------------------------------------------------
        // CHECK IF ALREADY ASSIGNED
        // -----------------------------------------------------

        if (ticket.getAssignedTechnician() != null
                && !ticket.getAssignedTechnician()
                .isBlank()) {

            throw new IllegalStateException(
                    "This ticket is already assigned."
            );
        }


        // -----------------------------------------------------
        // ASSIGN TECHNICIAN
        // -----------------------------------------------------

        ticket.setAssignedTechnician(
                technicianName
        );


        // -----------------------------------------------------
        // CHANGE STATUS
        // -----------------------------------------------------

        if (!"RESOLVED".equalsIgnoreCase(
                ticket.getStatus())) {

            ticket.setStatus("IN_PROGRESS");
        }


        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        return ticketRepository.save(ticket);
    }
}