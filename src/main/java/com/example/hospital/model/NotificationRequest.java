package com.example.hospital.model;

public record NotificationRequest(
        int appointmentId,
        int patientId,
        int doctorId,
        int slotId,
        int serviceId,
        String channel
) {
}
