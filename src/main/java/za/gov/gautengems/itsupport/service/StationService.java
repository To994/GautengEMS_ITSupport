package za.gov.gautengems.itsupport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.gov.gautengems.itsupport.entity.District;
import za.gov.gautengems.itsupport.entity.Station;
import za.gov.gautengems.itsupport.repository.StationRepository;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public StationService(
            StationRepository stationRepository) {

        this.stationRepository = stationRepository;
    }


    // =========================================================
    // GET ALL STATIONS
    // =========================================================

    public List<Station> getAllStations() {

        return stationRepository.findAll();
    }


    // =========================================================
    // GET ALL STATIONS FOR A DISTRICT
    // =========================================================

    public List<Station> getStationsByDistrict(
            District district) {

        return stationRepository
                .findByDistrictOrderByNameAsc(district);
    }


    // =========================================================
    // GET ACTIVE STATIONS FOR A DISTRICT
    // =========================================================

    public List<Station> getActiveStationsByDistrict(
            District district) {

        return stationRepository
                .findByDistrictAndActiveTrueOrderByNameAsc(
                        district
                );
    }


    // =========================================================
    // GET STATION BY ID
    // =========================================================

    public Station getStationById(Long id) {

        return stationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Station not found with ID: " + id
                        )
                );
    }


    // =========================================================
    // GET STATION BY ID AND DISTRICT
    // =========================================================

    public Station getStationByIdAndDistrict(
            Long id,
            District district) {

        return stationRepository
                .findByIdAndDistrict(id, district)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Station not found in the selected district."
                        )
                );
    }


    // =========================================================
    // CREATE STATION
    // =========================================================

    @Transactional
    public Station createStation(
            String name,
            String code,
            District district) {


        // -----------------------------------------------------
        // VALIDATE DISTRICT
        // -----------------------------------------------------

        if (district == null) {

            throw new IllegalArgumentException(
                    "A district is required."
            );
        }


        // -----------------------------------------------------
        // VALIDATE NAME
        // -----------------------------------------------------

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Station name is required."
            );
        }


        // -----------------------------------------------------
        // VALIDATE CODE
        // -----------------------------------------------------

        if (code == null || code.isBlank()) {

            throw new IllegalArgumentException(
                    "Station code is required."
            );
        }


        // -----------------------------------------------------
        // CLEAN INPUT
        // -----------------------------------------------------

        name = name.trim();

        code = code.trim().toUpperCase();


        // -----------------------------------------------------
        // CHECK DUPLICATE NAME
        // -----------------------------------------------------

        if (stationRepository
                .existsByDistrictAndName(
                        district,
                        name
                )) {

            throw new IllegalArgumentException(
                    "A station with this name already exists in "
                            + district.getName() + "."
            );
        }


        // -----------------------------------------------------
        // CHECK DUPLICATE CODE
        // -----------------------------------------------------

        if (stationRepository
                .existsByDistrictAndCode(
                        district,
                        code
                )) {

            throw new IllegalArgumentException(
                    "A station with this code already exists in "
                            + district.getName() + "."
            );
        }


        // -----------------------------------------------------
        // CREATE STATION
        // -----------------------------------------------------

        Station station = new Station();

        station.setName(name);

        station.setCode(code);

        station.setActive(true);

        station.setDistrict(district);


        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        return stationRepository.save(station);
    }


    // =========================================================
    // UPDATE STATION
    // =========================================================

    @Transactional
    public Station updateStation(
            Long id,
            String name,
            String code,
            District district) {


        // -----------------------------------------------------
        // VALIDATE DISTRICT
        // -----------------------------------------------------

        if (district == null) {

            throw new IllegalArgumentException(
                    "A district is required."
            );
        }


        // -----------------------------------------------------
        // VALIDATE NAME
        // -----------------------------------------------------

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Station name is required."
            );
        }


        // -----------------------------------------------------
        // VALIDATE CODE
        // -----------------------------------------------------

        if (code == null || code.isBlank()) {

            throw new IllegalArgumentException(
                    "Station code is required."
            );
        }


        // -----------------------------------------------------
        // CLEAN INPUT
        // -----------------------------------------------------

        name = name.trim();

        code = code.trim().toUpperCase();


        // -----------------------------------------------------
        // GET EXISTING STATION
        // -----------------------------------------------------

        Station station =
                getStationById(id);


        // -----------------------------------------------------
        // CHECK DUPLICATE NAME
        // -----------------------------------------------------

        boolean duplicateName =
                stationRepository
                        .existsByDistrictAndName(
                                district,
                                name
                        );


        if (duplicateName) {

            Station existingStation =
                    stationRepository
                            .findByIdAndDistrict(
                                    id,
                                    district
                            )
                            .orElse(null);


            if (existingStation == null
                    || !existingStation
                    .getId()
                    .equals(id)) {

                throw new IllegalArgumentException(
                        "A station with this name already exists in "
                                + district.getName() + "."
                );
            }
        }


        // -----------------------------------------------------
        // CHECK DUPLICATE CODE
        // -----------------------------------------------------

        boolean duplicateCode =
                stationRepository
                        .existsByDistrictAndCode(
                                district,
                                code
                        );


        if (duplicateCode) {

            Station existingStation =
                    stationRepository
                            .findByIdAndDistrict(
                                    id,
                                    district
                            )
                            .orElse(null);


            if (existingStation == null
                    || !existingStation
                    .getId()
                    .equals(id)) {

                throw new IllegalArgumentException(
                        "A station with this code already exists in "
                                + district.getName() + "."
                );
            }
        }


        // -----------------------------------------------------
        // UPDATE
        // -----------------------------------------------------

        station.setName(name);

        station.setCode(code);

        station.setDistrict(district);


        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        return stationRepository.save(station);
    }


    // =========================================================
    // ACTIVATE STATION
    // =========================================================

    @Transactional
    public Station activateStation(Long id) {

        Station station =
                getStationById(id);

        station.setActive(true);

        return stationRepository.save(station);
    }


    // =========================================================
    // DEACTIVATE STATION
    // =========================================================

    @Transactional
    public Station deactivateStation(Long id) {

        Station station =
                getStationById(id);

        station.setActive(false);

        return stationRepository.save(station);
    }


    // =========================================================
    // COUNT STATIONS
    // =========================================================

    public long countStations(
            District district) {

        return stationRepository
                .countByDistrict(district);
    }


    // =========================================================
    // COUNT ACTIVE STATIONS
    // =========================================================

    public long countActiveStations(
            District district) {

        return stationRepository
                .countByDistrictAndActiveTrue(
                        district
                );
    }

}