package com.example.hospital.controller;

import com.example.hospital.model.NotificationRequest;
import com.example.hospital.model.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock-api/notifications")
public class MockNotificationController {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationController.class);

    @PostMapping("/booking-confirmation")
    public ResponseEntity<NotificationResponse> sendBookingConfirmation(@RequestBody NotificationRequest request) {
        String externalReference = "NTF-" + request.appointmentId() + "-" + request.patientId();
        log.info("Mock notification service processed appointment {} for patient {}",
                request.appointmentId(), request.patientId());

        NotificationResponse response = new NotificationResponse(
                "SENT",
                "Confirmation notification queued for patient " + request.patientId(),
                externalReference
        );
        return ResponseEntity.ok(response);
    }
}
