package com.example.hospital.service;

import com.example.hospital.model.Appointment;
import com.example.hospital.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AppointmentTransactionService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentTransactionService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<Appointment> bookAppointment(int patientId, int doctorId, int slotId, int serviceId) {
        return appointmentRepository.bookAppointment(patientId, doctorId, slotId, serviceId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<Appointment> cancelAppointment(int appointmentId) {
        return appointmentRepository.cancelAppointment(appointmentId);
    }
}
