package za.gov.gautengems.itsupport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.gov.gautengems.itsupport.entity.District;
import za.gov.gautengems.itsupport.repository.DistrictRepository;

import java.util.List;

@Service
public class DistrictService {

    private final DistrictRepository districtRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DistrictService(
            DistrictRepository districtRepository) {

        this.districtRepository = districtRepository;
    }


    // =========================================================
    // GET ALL DISTRICTS
    // =========================================================

    public List<District> getAllDistricts() {

        return districtRepository.findAll();
    }


    // =========================================================
    // GET ACTIVE DISTRICTS
    // =========================================================

    public List<District> getActiveDistricts() {

        return districtRepository
                .findByActiveTrueOrderByNameAsc();
    }


    // =========================================================
    // GET ACTIVE DISTRICTS WITH STATIONS
    //
    // USED BY:
    //
    // Add User
    // Edit User
    //
    // EMS LOCATIONS IS THE SOURCE OF TRUTH.
    //
    // The repository loads the districts together with
    // their stations.
    // =========================================================

    @Transactional(readOnly = true)
    public List<District> getActiveDistrictsWithStations() {

        return districtRepository
                .findActiveDistrictsWithStations();
    }


    // =========================================================
    // GET DISTRICT BY ID
    // =========================================================

    public District getDistrictById(Long id) {

        return districtRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "District not found with ID: " + id
                        )
                );
    }


    // =========================================================
    // GET DISTRICT BY ID WITH STATIONS
    // =========================================================

    @Transactional(readOnly = true)
    public District getDistrictByIdWithStations(Long id) {

        return districtRepository
                .findByIdWithStations(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "District not found with ID: " + id
                        )
                );
    }


    // =========================================================
    // GET DISTRICT BY NAME
    // =========================================================

    public District getDistrictByName(String name) {

        return districtRepository
                .findByName(name)
                .orElse(null);
    }


    // =========================================================
    // GET DISTRICT BY CODE
    // =========================================================

    public District getDistrictByCode(String code) {

        return districtRepository
                .findByCode(code)
                .orElse(null);
    }


    // =========================================================
    // CREATE DISTRICT
    // =========================================================

    @Transactional
    public District createDistrict(
            String name,
            String code) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "District name is required."
            );
        }


        if (code == null || code.isBlank()) {

            throw new IllegalArgumentException(
                    "District code is required."
            );
        }


        name = name.trim();
        code = code.trim().toUpperCase();


        if (districtRepository.existsByName(name)) {

            throw new IllegalArgumentException(
                    "A district with this name already exists."
            );
        }


        if (districtRepository.existsByCode(code)) {

            throw new IllegalArgumentException(
                    "A district with this code already exists."
            );
        }


        District district = new District();

        district.setName(name);
        district.setCode(code);
        district.setActive(true);


        return districtRepository.save(district);
    }


    // =========================================================
    // UPDATE DISTRICT
    // =========================================================

    @Transactional
    public District updateDistrict(
            Long id,
            String name,
            String code) {

        District district =
                getDistrictById(id);


        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "District name is required."
            );
        }


        if (code == null || code.isBlank()) {

            throw new IllegalArgumentException(
                    "District code is required."
            );
        }


        name = name.trim();
        code = code.trim().toUpperCase();


        District existingByName =
                districtRepository
                        .findByName(name)
                        .orElse(null);


        if (existingByName != null
                && !existingByName.getId().equals(id)) {

            throw new IllegalArgumentException(
                    "Another district already uses this name."
            );
        }


        District existingByCode =
                districtRepository
                        .findByCode(code)
                        .orElse(null);


        if (existingByCode != null
                && !existingByCode.getId().equals(id)) {

            throw new IllegalArgumentException(
                    "Another district already uses this code."
            );
        }


        district.setName(name);
        district.setCode(code);


        return districtRepository.save(district);
    }


    // =========================================================
    // ACTIVATE DISTRICT
    // =========================================================

    @Transactional
    public District activateDistrict(Long id) {

        District district =
                getDistrictById(id);

        district.setActive(true);

        return districtRepository.save(district);
    }


    // =========================================================
    // DEACTIVATE DISTRICT
    // =========================================================

    @Transactional
    public District deactivateDistrict(Long id) {

        District district =
                getDistrictById(id);

        district.setActive(false);

        return districtRepository.save(district);
    }


    // =========================================================
    // DELETE DISTRICT
    // =========================================================

    /*
     * Districts are intentionally not deleted.
     *
     * Administrators should deactivate a district instead.
     */

}