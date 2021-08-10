package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.StationDTO;
import de.hhu.covidtracer.models.Station;
import de.hhu.covidtracer.services.StationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }


    @GetMapping
    public String stationsMainPage(Model model) {
        model.addAttribute(
                "stations",
                stationService.getAllDTOs());

        return "pages/stations/index_stations";
    }


    @GetMapping("/new")
    public String stationsAddNewPage(Model model) {
        model.addAttribute(
                "stationForm",
                new StationDTO());

        return "pages/stations/add_station";
    }


    @PostMapping("/new")
    public String stationsAddNew(
            @Valid @ModelAttribute("stationForm") StationDTO stationForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "pages/stations/add_station";
        stationService.saveNewEntityFromDTO(stationForm);

        return "redirect:/stations";
    }


    @GetMapping("/{stationId}/edit")
    public String stationsEditPage(
            @PathVariable long stationId,
            Model model) {
        model.addAttribute(
                "stationForm",
                stationService.getDTOById(stationId));

        return "pages/stations/edit_station";
    }


    @PostMapping("/{stationId}/edit")
    public String stationsEdit(
            @PathVariable long stationId,
            @Valid @ModelAttribute("stationForm") StationDTO stationForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            BeanUtils.copyProperties(
                    stationService.getDTOById(stationId), stationForm);

            return "pages/stations/edit_station";
        }

        stationForm.setId(stationId);
        stationService.update(stationForm);

        return "redirect:/stations";
    }


    @GetMapping("/{stationId}/delete")
    public String stationsDelete(@PathVariable long stationId) {
        Station station = stationService.getById(stationId);

        stationService.remove(station);

        return "redirect:/stations";
    }


    @GetMapping("/{stationId}/delete/confirm")
    public String stationsDeleteConfirm(
            @PathVariable long stationId,
            Model model) {
        model.addAttribute(
                "station",
                stationService.getDTOById(stationId));

        return "pages/stations/delete_station";
    }
}
