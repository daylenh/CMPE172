package com.example.hospital.service;

import com.example.hospital.model.NotificationRequest;
import com.example.hospital.model.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class MockNotificationGateway implements NotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationGateway.class);

    private final RestClient restClient;

    public MockNotificationGateway(
            RestClient.Builder restClientBuilder,
            @Value("${integration.notification.base-url:http://localhost:${server.port:8080}}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public NotificationResponse sendBookingConfirmation(NotificationRequest request) {
        try {
            NotificationResponse response = restClient.post()
                    .uri("/mock-api/notifications/booking-confirmation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(NotificationResponse.class);

            if (response == null) {
                throw new RestClientException("Mock notification service returned no body");
            }

            log.info("Notification service accepted appointment {} with external reference {}",
                    request.appointmentId(), response.externalReference());
            return response;
        } catch (RestClientException ex) {
            log.error("Notification call failed for appointment {}", request.appointmentId(), ex);
            return new NotificationResponse("FAILED", ex.getMessage(), "NOTIFICATION-UNAVAILABLE");
        }
    }
}
