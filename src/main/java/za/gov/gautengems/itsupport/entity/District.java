package za.gov.gautengems.itsupport.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "districts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name"),
                @UniqueConstraint(columnNames = "code")
        }
)
public class District {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // DISTRICT NAME
    // =========================================================

    @Column(
            nullable = false,
            unique = true
    )
    private String name;


    // =========================================================
    // DISTRICT CODE
    // =========================================================

    @Column(
            nullable = false,
            unique = true
    )
    private String code;


    // =========================================================
    // ACTIVE STATUS
    // =========================================================

    @Column(nullable = false)
    private boolean active = true;


    // =========================================================
    // STATIONS
    //
    // One district can have many EMS stations.
    //
    // This connects directly to:
    //
    // Station.district
    //
    // EMS Locations is therefore able to manage:
    //
    // District
    //      ↓
    // Stations
    // =========================================================

    @OneToMany(
            mappedBy = "district",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = false
    )
    @OrderBy("name ASC")
    private List<Station> stations = new ArrayList<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public District() {
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
    // GET STATIONS
    // =========================================================

    public List<Station> getStations() {
        return stations;
    }


    // =========================================================
    // SET STATIONS
    // =========================================================

    public void setStations(List<Station> stations) {

        this.stations =
                stations != null
                        ? stations
                        : new ArrayList<>();

    }


    // =========================================================
    // ADD STATION
    //
    // Keeps both sides of the relationship synchronized.
    // =========================================================

    public void addStation(Station station) {

        if (station == null) {
            return;
        }

        if (!stations.contains(station)) {
            stations.add(station);
        }

        station.setDistrict(this);
    }


    // =========================================================
    // REMOVE STATION
    // =========================================================

    public void removeStation(Station station) {

        if (station == null) {
            return;
        }

        stations.remove(station);

        if (station.getDistrict() == this) {
            station.setDistrict(null);
        }
    }
}