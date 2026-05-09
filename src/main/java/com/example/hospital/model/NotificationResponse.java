package com.example.hospital.model;

public record NotificationResponse(
        String status,
        String message,
        String externalReference
) {
}
