package za.gov.gautengems.itsupport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import za.gov.gautengems.itsupport.entity.District;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository
        extends JpaRepository<District, Long> {

    // =========================================================
    // FIND DISTRICT BY NAME
    // =========================================================

    Optional<District> findByName(String name);


    // =========================================================
    // FIND DISTRICT BY CODE
    // =========================================================

    Optional<District> findByCode(String code);


    // =========================================================
    // CHECK DISTRICT NAME
    // =========================================================

    boolean existsByName(String name);


    // =========================================================
    // CHECK DISTRICT CODE
    // =========================================================

    boolean existsByCode(String code);


    // =========================================================
    // GET ACTIVE DISTRICTS
    // =========================================================

    List<District> findByActiveTrueOrderByNameAsc();


    // =========================================================
    // GET ACTIVE DISTRICTS WITH ALL STATIONS
    //
    // We load the stations together with the districts.
    //
    // The HTML will only display ACTIVE stations.
    //
    // This avoids trying to serialize the JPA relationship
    // into JavaScript.
    // =========================================================

    @Query("""
            SELECT DISTINCT d
            FROM District d
            LEFT JOIN FETCH d.stations
            WHERE d.active = true
            ORDER BY d.name ASC
            """)
    List<District> findActiveDistrictsWithStations();


    // =========================================================
    // GET DISTRICT WITH ALL STATIONS
    // =========================================================

    @Query("""
            SELECT DISTINCT d
            FROM District d
            LEFT JOIN FETCH d.stations
            WHERE d.id = :id
            """)
    Optional<District> findByIdWithStations(
            @Param("id") Long id
    );
}