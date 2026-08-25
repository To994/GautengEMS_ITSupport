package za.gov.gautengems.itsupport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import za.gov.gautengems.itsupport.entity.District;
import za.gov.gautengems.itsupport.service.DistrictService;
import za.gov.gautengems.itsupport.service.StationService;

@Controller
@RequestMapping("/locations")
public class DistrictController {

    private final DistrictService districtService;
    private final StationService stationService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DistrictController(
            DistrictService districtService,
            StationService stationService) {

        this.districtService = districtService;
        this.stationService = stationService;
    }


    // =========================================================
    // LOCATION MANAGEMENT PAGE
    // URL: /locations
    // =========================================================

    @GetMapping
    public String locations(Model model) {

        model.addAttribute(
                "districts",
                districtService.getAllDistricts()
        );

        return "locations";
    }


    // =========================================================
    // CREATE DISTRICT
    // URL: /locations/district/save
    // =========================================================

    @PostMapping("/district/save")
    public String saveDistrict(
            @RequestParam String name,
            @RequestParam String code) {

        districtService.createDistrict(
                name,
                code
        );

        return "redirect:/locations";
    }


    // =========================================================
    // UPDATE DISTRICT
    // URL: /locations/district/update/{id}
    // =========================================================

    @PostMapping("/district/update/{id}")
    public String updateDistrict(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String code) {

        districtService.updateDistrict(
                id,
                name,
                code
        );

        return "redirect:/locations";
    }


    // =========================================================
    // ACTIVATE DISTRICT
    // URL: /locations/district/{id}/activate
    // =========================================================

    @PostMapping("/district/{id}/activate")
    public String activateDistrict(
            @PathVariable Long id) {

        districtService.activateDistrict(id);

        return "redirect:/locations";
    }


    // =========================================================
    // DEACTIVATE DISTRICT
    // URL: /locations/district/{id}/deactivate
    // =========================================================

    @PostMapping("/district/{id}/deactivate")
    public String deactivateDistrict(
            @PathVariable Long id) {

        districtService.deactivateDistrict(id);

        return "redirect:/locations";
    }


    // =========================================================
    // CREATE STATION
    // URL: /locations/station/save
    // =========================================================

    @PostMapping("/station/save")
    public String saveStation(
            @RequestParam String name,
            @RequestParam String code,
            @RequestParam Long districtId) {

        District district =
                districtService.getDistrictById(
                        districtId
                );

        stationService.createStation(
                name,
                code,
                district
        );

        return "redirect:/locations";
    }


    // =========================================================
    // UPDATE STATION
    // URL: /locations/station/update/{id}
    // =========================================================

    @PostMapping("/station/update/{id}")
    public String updateStation(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String code,
            @RequestParam Long districtId) {

        District district =
                districtService.getDistrictById(
                        districtId
                );

        stationService.updateStation(
                id,
                name,
                code,
                district
        );

        return "redirect:/locations";
    }


    // =========================================================
    // ACTIVATE STATION
    // URL: /locations/station/{id}/activate
    // =========================================================

    @PostMapping("/station/{id}/activate")
    public String activateStation(
            @PathVariable Long id) {

        stationService.activateStation(id);

        return "redirect:/locations";
    }


    // =========================================================
    // DEACTIVATE STATION
    // URL: /locations/station/{id}/deactivate
    // =========================================================

    @PostMapping("/station/{id}/deactivate")
    public String deactivateStation(
            @PathVariable Long id) {

        stationService.deactivateStation(id);

        return "redirect:/locations";
    }
}