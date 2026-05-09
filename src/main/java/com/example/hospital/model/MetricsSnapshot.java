package com.example.hospital.model;

public record MetricsSnapshot(
        long totalBookings,
        long successfulBookings,
        long failedBookings,
        long successfulCancellations,
        long failedCancellations,
        double averageBookingLatencyMillis
) {
}
