package za.gov.gautengems.itsupport.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // PERSONAL INFORMATION
    // =========================================================

    @Column(name = "personal_number", unique = true, nullable = false)
    private String personalNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;


    // =========================================================
    // EMS LOCATION INFORMATION
    // =========================================================

    @Column(nullable = false)
    private String district;

    @Column(name = "station_unit", nullable = false)
    private String stationUnit;


    // =========================================================
    // DEPARTMENT
    // =========================================================

    @Column(nullable = false)
    private String department;


    // =========================================================
    // LOGIN INFORMATION
    // =========================================================

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;


    // =========================================================
    // USER ROLE
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    // =========================================================
    // ACCOUNT STATUS
    // =========================================================

    @Column(nullable = false)
    private boolean active = true;


    // =========================================================
    // ROLES
    // =========================================================

    public enum Role {

        ADMIN,

        TECHNICIAN,

        STATION_MANAGER
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public User() {
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }


    // =========================================================
    // GET PERSONAL NUMBER
    // =========================================================

    public String getPersonalNumber() {
        return personalNumber;
    }


    // =========================================================
    // SET PERSONAL NUMBER
    // =========================================================

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }


    // =========================================================
    // GET FIRST NAME
    // =========================================================

    public String getFirstName() {
        return firstName;
    }


    // =========================================================
    // SET FIRST NAME
    // =========================================================

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    // =========================================================
    // GET SURNAME
    // =========================================================

    public String getSurname() {
        return surname;
    }


    // =========================================================
    // SET SURNAME
    // =========================================================

    public void setSurname(String surname) {
        this.surname = surname;
    }


    // =========================================================
    // GET EMAIL
    // =========================================================

    public String getEmail() {
        return email;
    }


    // =========================================================
    // SET EMAIL
    // =========================================================

    public void setEmail(String email) {
        this.email = email;
    }


    // =========================================================
    // GET PHONE
    // =========================================================

    public String getPhone() {
        return phone;
    }


    // =========================================================
    // SET PHONE
    // =========================================================

    public void setPhone(String phone) {
        this.phone = phone;
    }


    // =========================================================
    // GET DISTRICT
    // =========================================================

    public String getDistrict() {
        return district;
    }


    // =========================================================
    // SET DISTRICT
    // =========================================================

    public void setDistrict(String district) {
        this.district = district;
    }


    // =========================================================
    // GET EMS STATION
    // =========================================================

    public String getStationUnit() {
        return stationUnit;
    }


    // =========================================================
    // SET EMS STATION
    // =========================================================

    public void setStationUnit(String stationUnit) {
        this.stationUnit = stationUnit;
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
    // GET USERNAME
    // =========================================================

    public String getUsername() {
        return username;
    }


    // =========================================================
    // SET USERNAME
    // =========================================================

    public void setUsername(String username) {
        this.username = username;
    }


    // =========================================================
    // GET PASSWORD
    // =========================================================

    public String getPassword() {
        return password;
    }


    // =========================================================
    // SET PASSWORD
    // =========================================================

    public void setPassword(String password) {
        this.password = password;
    }


    // =========================================================
    // GET ROLE
    // =========================================================

    public Role getRole() {
        return role;
    }


    // =========================================================
    // SET ROLE
    // =========================================================

    public void setRole(Role role) {
        this.role = role;
    }


    // =========================================================
    // GET ACTIVE
    // =========================================================

    public boolean isActive() {
        return active;
    }


    // =========================================================
    // SET ACTIVE
    // =========================================================

    public void setActive(boolean active) {
        this.active = active;
    }
}