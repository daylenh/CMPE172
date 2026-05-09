package com.example.hospital.model;

public record BookingResult(
        boolean booked,
        Appointment appointment,
        boolean notificationSent,
        String notificationMessage
) {
    public static BookingResult bookingFailed() {
        return new BookingResult(false, null, false, "Slot is no longer available.");
    }
}
