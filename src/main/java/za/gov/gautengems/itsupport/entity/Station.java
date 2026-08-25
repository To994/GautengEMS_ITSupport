package za.gov.gautengems.itsupport.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "stations",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"district_id", "name"}
                ),
                @UniqueConstraint(
                        columnNames = {"district_id", "code"}
                )
        }
)
public class Station {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // STATION NAME
    // =========================================================

    @Column(nullable = false)
    private String name;


    // =========================================================
    // STATION CODE
    // =========================================================

    @Column(nullable = false)
    private String code;


    // =========================================================
    // ACTIVE STATUS
    // =========================================================

    @Column(nullable = false)
    private boolean active = true;


    // =========================================================
    // DISTRICT
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "district_id",
            nullable = false
    )
    private District district;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Station() {
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }


    // =========================================================
    // GET NAME
    // =========================================================

    public String getName() {
        return name;
    }


    // =========================================================
    // SET NAME
    // =========================================================

    public void setName(String name) {
        this.name = name;
    }


    // =========================================================
    // GET CODE
    // =========================================================

    public String getCode() {
        return code;
    }


    // =========================================================
    // SET CODE
    // =========================================================

    public void setCode(String code) {
        this.code = code;
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


    // =========================================================
    // GET DISTRICT
    // =========================================================

    public District getDistrict() {
        return district;
    }


    // =========================================================
    // SET DISTRICT
    // =========================================================

    public void setDistrict(District district) {
        this.district = district;
    }
}