package com.example.hospital.repository;

import com.example.hospital.model.Availability;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Repository
public class AvailabilityRepository {

    private final JdbcTemplate jdbcTemplate;

    public AvailabilityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Availability> findAll() {
        String sql = "SELECT * FROM Availability ORDER BY Date, StartTime, SlotID";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Availability(
                        rs.getInt("SlotID"),
                        rs.getInt("DoctorID"),
                        rs.getDate("Date"),
                        rs.getTime("StartTime"),
                        rs.getTime("EndTime"),
                        rs.getInt("ServiceID"),
                        rs.getString("Status")
                )
        );
    }

    public List<Availability> findAvailable() {
        String sql = """
                SELECT *
                FROM Availability
                WHERE Status = 'Available'
                ORDER BY Date, StartTime, SlotID
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Availability(
                        rs.getInt("SlotID"),
                        rs.getInt("DoctorID"),
                        rs.getDate("Date"),
                        rs.getTime("StartTime"),
                        rs.getTime("EndTime"),
                        rs.getInt("ServiceID"),
                        rs.getString("Status")
                )
        );
    }

    public int createSlot(int doctorId, Date date, Time startTime, Time endTime, int serviceId) {
        String sql = """
                INSERT INTO Availability (DoctorID, Date, StartTime, EndTime, ServiceID, Status)
                VALUES (?, ?, ?, ?, ?, 'Available')
                """;
        return jdbcTemplate.update(sql, doctorId, date, startTime, endTime, serviceId);
    }
}
