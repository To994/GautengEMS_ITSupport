package za.gov.gautengems.itsupport.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    private String priority;

    private String status;

    private String department;

    private String requesterName;

    private String requesterEmail;

    private String assignedTechnician;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Ticket() {
    }


    // =========================================================
    // CREATE TICKET
    // =========================================================

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null || status.isBlank()) {
            status = "OPEN";
        }

        if (priority == null || priority.isBlank()) {
            priority = "MEDIUM";
        }
    }


    // =========================================================
    // UPDATE TICKET
    // =========================================================

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }


    // =========================================================
    // SET ID
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }


    // =========================================================
    // GET TICKET NUMBER
    // =========================================================

    public String getTicketNumber() {
        return ticketNumber;
    }


    // =========================================================
    // SET TICKET NUMBER
    // =========================================================

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }


    // =========================================================
    // GET TITLE
    // =========================================================

    public String getTitle() {
        return title;
    }


    // =========================================================
    // SET TITLE
    // =========================================================

    public void setTitle(String title) {
        this.title = title;
    }


    // =========================================================
    // GET DESCRIPTION
    // =========================================================

    public String getDescription() {
        return description;
    }


    // =========================================================
    // SET DESCRIPTION
    // =========================================================

    public void setDescription(String description) {
        this.description = description;
    }


    // =========================================================
    // GET CATEGORY
    // =========================================================

    public String getCategory() {
        return category;
    }


    // =========================================================
    // SET CATEGORY
    // =========================================================

    public void setCategory(String category) {
        this.category = category;
    }


    // =========================================================
    // GET PRIORITY
    // =========================================================

    public String getPriority() {
        return priority;
    }


    // =========================================================
    // SET PRIORITY
    // =========================================================

    public void setPriority(String priority) {
        this.priority = priority;
    }


    // =========================================================
    // GET STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }


    // =========================================================
    // SET STATUS
    // =========================================================

    public void setStatus(String status) {
        this.status = status;
    }


    // =========================================================
    // GET DEPARTMENT
    // =========================================================

    public String getDepartment() {
        return department;
    }


    // =========================================================
    // SET DEPARTMENT
    // =========================================================

    public void setDepartment(String department) {
        this.department = department;
    }


    // =========================================================
    // GET REQUESTER NAME
    // =========================================================

    public String getRequesterName() {
        return requesterName;
    }


    // =========================================================
    // SET REQUESTER NAME
    // =========================================================

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }


    // =========================================================
    // GET REQUESTER EMAIL
    // =========================================================

    public String getRequesterEmail() {
        return requesterEmail;
    }


    // =========================================================
    // SET REQUESTER EMAIL
    // =========================================================

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }


    // =========================================================
    // GET ASSIGNED TECHNICIAN
    // =========================================================

    public String getAssignedTechnician() {
        return assignedTechnician;
    }


    // =========================================================
    // SET ASSIGNED TECHNICIAN
    // =========================================================

    public void setAssignedTechnician(String assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }


    // =========================================================
    // GET CREATED AT
    // =========================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    // =========================================================
    // SET CREATED AT
    // =========================================================

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // =========================================================
    // GET UPDATED AT
    // =========================================================

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    // =========================================================
    // SET UPDATED AT
    // =========================================================

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}