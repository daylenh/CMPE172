    package com.example.hospital.service;

    import com.example.hospital.model.Appointment;
    import com.example.hospital.repository.AppointmentRepository;
    import org.springframework.stereotype.Service;
    import java.util.*;

    @Service
    public class AppointmentService {
        private final AppointmentRepository repository;

        public AppointmentService(AppointmentRepository repository){
            this.repository = repository;
        }

        public List<Appointment> getAppointments() {
            return repository.findAll();
        }

    }
