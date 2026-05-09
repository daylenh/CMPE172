package com.example.hospital.service;

import com.example.hospital.model.NotificationRequest;
import com.example.hospital.model.NotificationResponse;

public interface NotificationGateway {
    NotificationResponse sendBookingConfirmation(NotificationRequest request);
}
