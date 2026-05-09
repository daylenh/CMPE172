package com.example.hospital.model;

public record HealthResponse(
        String status,
        String database,
        String notificationService,
        MetricsSnapshot metrics
) {
}
