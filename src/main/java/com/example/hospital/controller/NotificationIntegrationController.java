package com.example.hospital.controller;

import com.example.hospital.model.Appointment;
import com.example.hospital.model.NotificationResponse;
import com.example.hospital.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/integrations")
public class NotificationIntegrationController {

    private final AppointmentService appointmentService;

    public NotificationIntegrationController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments/{appointmentId}/notifications")
    public ResponseEntity<NotificationResponse> sendNotification(@PathVariable int appointmentId) {
        Appointment appointment = appointmentService.getAppointment(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Appointment not found: " + appointmentId));

        NotificationResponse response = appointmentService.notifyAppointmentBooked(appointment);
        return ResponseEntity.ok(response);
    }
}
