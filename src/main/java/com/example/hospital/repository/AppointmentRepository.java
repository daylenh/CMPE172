package com.example.hospital.repository;
import org.springframework.stereotype.Repository;
import com.example.hospital.model.Appointment;
import java.util.Arrays;
import java.util.List;


@Repository
public class AppointmentRepository {
    public List<Appointment> findAll(){
        return Arrays.asList(
                new Appointment ("Dr. Smith", "Mar 15", "9:00 AM"),
                new Appointment("Dr. Lee", "Mar 16", "10:00 AM")
        );
    }
}
