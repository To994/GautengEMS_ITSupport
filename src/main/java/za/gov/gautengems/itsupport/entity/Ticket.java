package za.gov.gautengems.itsupport.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // TICKET NUMBER
    // =========================================================

    @Column(nullable = false, unique = true)
    private String ticketNumber;


    // =========================================================
    // ISSUE INFORMATION
    // =========================================================

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    private String problemType;

    private String deviceType;

    private String assetNumber;

    private String priority;

    private String status;


    // =========================================================
    // STATION / UNIT INFORMATION
    // =========================================================

    private String department;

    private String district;

    private String stationUnit;


    // =========================================================
    // STATION MANAGER INFORMATION
    // =========================================================

    private String managerFirstName;

    private String managerSurname;

    private String managerEmail;

    private String managerPhone;

    private String managerRole;

    private String managerIpAddress;


    // =========================================================
    // LEGACY REQUESTER FIELDS
    //
    // Kept so existing parts of the system do not break.
    // These can be phased out later once the whole system
    // has been converted to the Station Manager model.
    // =========================================================

    private String requesterName;

    private String requesterEmail;


    // =========================================================
    // TECHNICIAN
    // =========================================================

    private String assignedTechnician;


    // =========================================================
    // DATES
    // =========================================================

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
    // ID
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    // =========================================================
    // TICKET NUMBER
    // =========================================================

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }


    // =========================================================
    // TITLE
    // =========================================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    // =========================================================
    // DESCRIPTION
    // =========================================================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // =========================================================
    // CATEGORY
    // =========================================================

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    // =========================================================
    // PROBLEM TYPE
    // =========================================================

    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }


    // =========================================================
    // DEVICE TYPE
    // =========================================================

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }


    // =========================================================
    // ASSET NUMBER
    // =========================================================

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }


    // =========================================================
    // PRIORITY
    // =========================================================

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }


    // =========================================================
    // STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // =========================================================
    // DEPARTMENT
    // =========================================================

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


    // =========================================================
    // DISTRICT
    // =========================================================

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }


    // =========================================================
    // STATION / UNIT
    // =========================================================

    public String getStationUnit() {
        return stationUnit;
    }

    public void setStationUnit(String stationUnit) {
        this.stationUnit = stationUnit;
    }


    // =========================================================
    // MANAGER FIRST NAME
    // =========================================================

    public String getManagerFirstName() {
        return managerFirstName;
    }

    public void setManagerFirstName(String managerFirstName) {
        this.managerFirstName = managerFirstName;
    }


    // =========================================================
    // MANAGER SURNAME
    // =========================================================

    public String getManagerSurname() {
        return managerSurname;
    }

    public void setManagerSurname(String managerSurname) {
        this.managerSurname = managerSurname;
    }


    // =========================================================
    // MANAGER EMAIL
    // =========================================================

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }


    // =========================================================
    // MANAGER PHONE
    // =========================================================

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }


    // =========================================================
    // MANAGER ROLE
    // =========================================================

    public String getManagerRole() {
        return managerRole;
    }

    public void setManagerRole(String managerRole) {
        this.managerRole = managerRole;
    }


    // =========================================================
    // MANAGER IP ADDRESS
    // =========================================================

    public String getManagerIpAddress() {
        return managerIpAddress;
    }

    public void setManagerIpAddress(String managerIpAddress) {
        this.managerIpAddress = managerIpAddress;
    }


    // =========================================================
    // LEGACY REQUESTER NAME
    // =========================================================

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }


    // =========================================================
    // LEGACY REQUESTER EMAIL
    // =========================================================

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }


    // =========================================================
    // ASSIGNED TECHNICIAN
    // =========================================================

    public String getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(String assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }


    // =========================================================
    // CREATED AT
    // =========================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // =========================================================
    // UPDATED AT
    // =========================================================

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}