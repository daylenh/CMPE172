package com.example.hospital.controller;

import com.example.hospital.model.Appointment;
import com.example.hospital.model.BookingResult;
import com.example.hospital.service.AppointmentService;
import com.example.hospital.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class AppointmentController {


    private final AppointmentService service;
    private final AvailabilityService availabilityService;

    public AppointmentController(AppointmentService service, AvailabilityService availabilityService) {
        this.service = service;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        List<Appointment> list = service.getAppointments();
        model.addAttribute("appointments", list);
        return "appointments";
    }

    @GetMapping("/book")
    public String bookForm(Model model) {
        model.addAttribute("slots", availabilityService.getAvailableSlots());
        return "book";
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam int patientId,
                                  @RequestParam int doctorId,
                                  @RequestParam int slotId,
                                  @RequestParam int serviceId,
                                  RedirectAttributes redirectAttributes) {

        BookingResult result = service.bookAppointment(patientId, doctorId, slotId, serviceId);

        if (result.booked()) {
            redirectAttributes.addFlashAttribute("bookingResult", result);
            return "redirect:/confirmation";
        } else {
            return "redirect:/book?error=true";
        }
    }

    @PostMapping("/appointments/{appointmentId}/cancel")
    public String cancelAppointment(@PathVariable int appointmentId, RedirectAttributes redirectAttributes) {
        Optional<Appointment> canceled = service.cancelAppointment(appointmentId);
        redirectAttributes.addFlashAttribute("cancelMessage", canceled.isPresent()
                ? "Appointment " + appointmentId + " was canceled and the slot is available again."
                : "Appointment " + appointmentId + " could not be canceled.");
        return "redirect:/appointments";
    }

    @PostMapping("/api/appointments/{appointmentId}/cancel")
    public ResponseEntity<Appointment> cancelAppointmentApi(@PathVariable int appointmentId) {
        return service.cancelAppointment(appointmentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/confirmation")
    public String confirmation(Model model) {
        if (!model.containsAttribute("bookingResult")) {
            model.addAttribute("bookingResult", BookingResult.bookingFailed());
        }
        return "confirmation";
    }
}
