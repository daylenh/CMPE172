package com.example.hospital.repository;

import com.example.hospital.model.Appointment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppointmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GET ALL
    public List<Appointment> findAll() {
        String sql = "SELECT * FROM Appointment ORDER BY CreatedAt DESC, AppointmentID DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Appointment(
                rs.getInt("AppointmentID"),
                rs.getInt("PatientID"),
                rs.getInt("DoctorID"),
                rs.getInt("SlotID"),
                rs.getInt("ServiceID"),
                rs.getString("AppointmentStatus")
        ));
    }

    public Optional<Appointment> findById(int appointmentId) {
        String sql = "SELECT * FROM Appointment WHERE AppointmentID = ?";

        List<Appointment> appointments = jdbcTemplate.query(sql, (rs, rowNum) -> new Appointment(
                        rs.getInt("AppointmentID"),
                        rs.getInt("PatientID"),
                        rs.getInt("DoctorID"),
                        rs.getInt("SlotID"),
                        rs.getInt("ServiceID"),
                        rs.getString("AppointmentStatus")
                ),
                appointmentId
        );
        return appointments.stream().findFirst();
    }

    public Optional<Appointment> bookAppointment(int patientId, int doctorId, int slotId, int serviceId) {
        String checkSql = """
                SELECT DoctorID, ServiceID, Status
                FROM Availability
                WHERE SlotID = ?
                FOR UPDATE
                """;
        String insertSql = "INSERT INTO Appointment (PatientID, DoctorID, SlotID, ServiceID, AppointmentStatus) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE Availability SET Status = 'Booked' WHERE SlotID = ? AND Status = 'Available'";

        List<SlotSnapshot> results = jdbcTemplate.query(checkSql, (rs, rowNum) ->
                        new SlotSnapshot(
                                rs.getInt("DoctorID"),
                                rs.getInt("ServiceID"),
                                rs.getString("Status")
                        ),
                slotId
        );
        if (results.isEmpty()) {
            return Optional.empty();
        }

        SlotSnapshot slot = results.getFirst();
        if (!"Available".equalsIgnoreCase(slot.status())
                || slot.doctorId() != doctorId
                || slot.serviceId() != serviceId) {
            return Optional.empty();
        }

        int updatedSlots = jdbcTemplate.update(updateSql, slotId);
        if (updatedSlots != 1) {
            return Optional.empty();
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(insertSql, new String[]{"AppointmentID"});
            statement.setInt(1, patientId);
            statement.setInt(2, doctorId);
            statement.setInt(3, slotId);
            statement.setInt(4, serviceId);
            statement.setString(5, "BOOKED");
            return statement;
        }, keyHolder);

        Number appointmentId = keyHolder.getKey();
        if (appointmentId == null) {
            return Optional.empty();
        }

        return Optional.of(new Appointment(
                appointmentId.intValue(),
                patientId,
                doctorId,
                slotId,
                serviceId,
                "BOOKED"
        ));
    }

    public Optional<Appointment> cancelAppointment(int appointmentId) {
        String lockSql = "SELECT * FROM Appointment WHERE AppointmentID = ? FOR UPDATE";
        List<Appointment> appointments = jdbcTemplate.query(lockSql, (rs, rowNum) -> new Appointment(
                rs.getInt("AppointmentID"),
                rs.getInt("PatientID"),
                rs.getInt("DoctorID"),
                rs.getInt("SlotID"),
                rs.getInt("ServiceID"),
                rs.getString("AppointmentStatus")
        ), appointmentId);

        if (appointments.isEmpty()) {
            return Optional.empty();
        }

        Appointment appointment = appointments.getFirst();
        if (!"BOOKED".equalsIgnoreCase(appointment.getStatus())) {
            return Optional.empty();
        }

        // Delete the appointment from the database
        int deletedRows = jdbcTemplate.update("""
                DELETE FROM Appointment
                WHERE AppointmentID = ?
                """, appointmentId);

        if (deletedRows != 1) {
            return Optional.empty();
        }

        // Release the slot back to available
        jdbcTemplate.update("""
                UPDATE Availability
                SET Status = 'Available'
                WHERE SlotID = ?
                """, appointment.getSlotID());

        appointment.setStatus("CANCELED");
        return Optional.of(appointment);
    }

    private record SlotSnapshot(int doctorId, int serviceId, String status) {
    }
}
