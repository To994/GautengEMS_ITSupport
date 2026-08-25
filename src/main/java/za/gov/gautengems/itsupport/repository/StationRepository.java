package za.gov.gautengems.itsupport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import za.gov.gautengems.itsupport.entity.District;
import za.gov.gautengems.itsupport.entity.Station;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository
        extends JpaRepository<Station, Long> {

    // =========================================================
    // GET ALL STATIONS FOR A DISTRICT
    // =========================================================

    List<Station> findByDistrictOrderByNameAsc(
            District district
    );


    // =========================================================
    // GET ACTIVE STATIONS FOR A DISTRICT
    // =========================================================

    List<Station> findByDistrictAndActiveTrueOrderByNameAsc(
            District district
    );


    // =========================================================
    // FIND STATION BY ID AND DISTRICT
    // =========================================================

    Optional<Station> findByIdAndDistrict(
            Long id,
            District district
    );


    // =========================================================
    // CHECK STATION NAME IN DISTRICT
    // =========================================================

    boolean existsByDistrictAndName(
            District district,
            String name
    );


    // =========================================================
    // CHECK STATION CODE IN DISTRICT
    // =========================================================

    boolean existsByDistrictAndCode(
            District district,
            String code
    );


    // =========================================================
    // COUNT STATIONS IN DISTRICT
    // =========================================================

    long countByDistrict(District district);


    // =========================================================
    // COUNT ACTIVE STATIONS IN DISTRICT
    // =========================================================

    long countByDistrictAndActiveTrue(
            District district
    );
}