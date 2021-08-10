package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.AppointmentDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.models.Appointment;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.services.AppointmentService;
import de.hhu.covidtracer.services.IndexPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final IndexPersonService indexPersonService;

    @Autowired
    public AppointmentController(
            AppointmentService appointmentService,
            IndexPersonService indexPersonService) {
        this.appointmentService = appointmentService;
        this.indexPersonService = indexPersonService;
    }


    @GetMapping("/tests")
    public String appointmentMainPage(Model model) {
        //model.addAttribute("futureAppointments", appointmentService.getAllByTodaysOrFutureDateTime());
        //model.addAttribute("missedAppointments", appointmentService.getAllByMissedAppointments());
        model.addAttribute(
                "staff",
                indexPersonService.getAllDTOsByStatus(Status.STAFF));
        return "pages/appointments/index_appointments";
    }


    @GetMapping("/index/{idxId}/tests")
    public String appointmentPatientPage(Model model, @PathVariable long idxId) {
        model.addAttribute(
                "person",
                indexPersonService.getDTOById(idxId));
        model.addAttribute(
                "appointments",
                appointmentService.getAllDTOsById(idxId));
        return "pages/appointments/show_appointment";
    }


    @GetMapping("/index/{idxId}/tests/new")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String appointmentAddNewPage(
            @PathVariable long idxId,
            Model model) {
        model.addAttribute(
                "person",
                indexPersonService.getDTOById(idxId));
        model.addAttribute(
                "appointmentForm",
                new AppointmentDTO());

        return "pages/appointments/add_appointment";
    }


    @PostMapping("/index/{idxId}/tests/new")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String appointmentAddNew(
            @PathVariable long idxId,
            @ModelAttribute("person") IndexPersonDTO person,
            @Valid @ModelAttribute("appointmentForm") AppointmentDTO appointmentForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "pages/appointments/add_appointment";
        }

        appointmentForm.setId(0L);
        Appointment appointment = appointmentService
                .createFromDTO(appointmentForm);

        indexPersonService
                .saveAppointmentToIndex(appointment, person.getId());

        return "redirect:/index/" + idxId + "/tests";
    }


    @GetMapping("/tests/{appointmentId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String appointmentEditPage(
            @PathVariable long appointmentId,
            Model model) {
        model.addAttribute(
                "appointmentForm",
                appointmentService.getDTOById(appointmentId));

        return "pages/appointments/edit_appointment";
    }


    @PostMapping("/tests/{appointmentId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String appointmentEdit(
            @PathVariable long appointmentId,
            @Valid @ModelAttribute("appointmentForm") AppointmentDTO appointmentForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "pages/appointments/edit_appointment";
        }

        Appointment appointment = appointmentService
                .update(appointmentForm, appointmentId);
        String redirectUrl = "/tests";

        if (appointment.getIndexPerson() != null) {
            long indexPersonId = appointment
                    .getIndexPerson()
                    .getId();
            redirectUrl = "/index/" + indexPersonId + "/tests";
        }

        return "redirect:" + redirectUrl;
    }


    @GetMapping("/tests/{appointmentId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String appointmentDelete(
            @PathVariable long appointmentId) {
        Appointment appointment = appointmentService
                .getById(appointmentId);
        String redirectUrl = "/tests";

        if (appointment.getIndexPerson() != null) {
            long indexPersonId = appointment
                    .getIndexPerson()
                    .getId();
            redirectUrl = "/index/" + indexPersonId + "/tests/";
        }

        appointmentService.delete(appointment);

        return "redirect:" + redirectUrl;
    }
}
