package com.example.hospital.service;

import com.example.hospital.model.Availability;
import com.example.hospital.repository.AvailabilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Service
public class AvailabilityService {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityService.class);

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public List<Availability> getAllSlots() {
        return availabilityRepository.findAll();
    }

    public List<Availability> getAvailableSlots() {
        return availabilityRepository.findAvailable();
    }

    public void createSlot(int doctorId, Date date, Time startTime, Time endTime, int serviceId) {
        log.info("Provider {} is creating availability on {} from {} to {} for service {}",
                doctorId, date, startTime, endTime, serviceId);
        availabilityRepository.createSlot(doctorId, date, startTime, endTime, serviceId);
    }
}
