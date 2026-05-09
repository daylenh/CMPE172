package com.example.hospital.controller;

import com.example.hospital.model.HealthResponse;
import com.example.hospital.model.MetricsSnapshot;
import com.example.hospital.repository.SystemRepository;
import com.example.hospital.service.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SystemManagementController {

    private static final Logger log = LoggerFactory.getLogger(SystemManagementController.class);

    private final MonitoringService monitoringService;
    private final SystemRepository systemRepository;

    public SystemManagementController(MonitoringService monitoringService, SystemRepository systemRepository) {
        this.monitoringService = monitoringService;
        this.systemRepository = systemRepository;
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        MetricsSnapshot metrics = monitoringService.snapshot();
        try {
            boolean databaseHealthy = systemRepository.isDatabaseReachable();
            HealthResponse response = new HealthResponse(
                    databaseHealthy ? "UP" : "DEGRADED",
                    databaseHealthy ? "UP" : "DOWN",
                    "MOCKED",
                    metrics
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            log.error("Health check failed while probing the database", ex);
            HealthResponse response = new HealthResponse(
                    "DOWN",
                    "DOWN",
                    "UNKNOWN",
                    metrics
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}
