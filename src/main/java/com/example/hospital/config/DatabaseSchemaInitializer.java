package com.example.hospital.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.schema-initializer.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSchemaInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS Users (
                    UserID INT AUTO_INCREMENT PRIMARY KEY,
                    FirstName VARCHAR(50) NOT NULL,
                    LastName VARCHAR(50) NOT NULL,
                    Email VARCHAR(100) UNIQUE,
                    PhoneNumber VARCHAR(20),
                    RoleType VARCHAR(15) NOT NULL,
                    Password VARCHAR(255) NOT NULL
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS Services (
                    ServiceID INT AUTO_INCREMENT PRIMARY KEY,
                    ServiceName VARCHAR(100) NOT NULL,
                    DurationMinutes TIME NOT NULL,
                    Description VARCHAR(255) NOT NULL
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS Doctor (
                    DoctorID INT PRIMARY KEY,
                    LicenseNumber VARCHAR(50) NOT NULL,
                    CONSTRAINT fk_doctor_user
                        FOREIGN KEY (DoctorID) REFERENCES Users(UserID)
                        ON DELETE CASCADE
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS Patients (
                    PatientID INT PRIMARY KEY,
                    Gender ENUM('Male', 'Female', 'Other'),
                    DateOfBirth DATE NOT NULL,
                    Address VARCHAR(100) NOT NULL,
                    CONSTRAINT fk_patients_user
                        FOREIGN KEY (PatientID) REFERENCES Users(UserID)
                        ON DELETE CASCADE
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS Admins (
                    AdminID INT PRIMARY KEY,
                    AdminLevel INT NOT NULL,
                    CONSTRAINT fk_admins_user
                        FOREIGN KEY (AdminID) REFERENCES Users(UserID)
                        ON DELETE CASCADE
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS Specializations (
                    DoctorID INT NOT NULL,
                    SpecializationName VARCHAR(50) NOT NULL,
                    CertificationDate DATE NOT NULL,
                    PRIMARY KEY (DoctorID, SpecializationName),
                    CONSTRAINT fk_specializations_doctor
                        FOREIGN KEY (DoctorID) REFERENCES Doctor(DoctorID)
                        ON DELETE CASCADE
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS Availability (
                    SlotID INT AUTO_INCREMENT PRIMARY KEY,
                    DoctorID INT NOT NULL,
                    Date DATE NOT NULL,
                    StartTime TIME NOT NULL,
                    EndTime TIME NOT NULL,
                    ServiceID INT,
                    Status ENUM('Not Available', 'Available', 'Booked') DEFAULT 'Available',
                    CONSTRAINT fk_availability_doctor
                        FOREIGN KEY (DoctorID) REFERENCES Doctor(DoctorID)
                        ON DELETE CASCADE,
                    CONSTRAINT fk_availability_service
                        FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID)
                        ON DELETE SET NULL,
                    CONSTRAINT uq_availability_slot UNIQUE (DoctorID, Date, StartTime)
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS Appointment (
                    AppointmentID INT AUTO_INCREMENT PRIMARY KEY,
                    PatientID INT NOT NULL,
                    DoctorID INT NOT NULL,
                    SlotID INT,
                    ServiceID INT,
                    AppointmentStatus VARCHAR(20),
                    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_appointment_patient
                        FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
                        ON DELETE CASCADE,
                    CONSTRAINT fk_appointment_doctor
                        FOREIGN KEY (DoctorID) REFERENCES Doctor(DoctorID)
                        ON DELETE CASCADE,
                    CONSTRAINT fk_appointment_slot
                        FOREIGN KEY (SlotID) REFERENCES Availability(SlotID)
                        ON DELETE SET NULL,
                    CONSTRAINT fk_appointment_service
                        FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID)
                        ON DELETE SET NULL
                )
                """);

        seedDemoData();
    }

    private void seedDemoData() {
        // Users table - expanded with more patients and doctors
        jdbcTemplate.update("""
            INSERT IGNORE INTO Users
                (UserID, FirstName, LastName, Email, PhoneNumber, RoleType, Password)
            VALUES
                (1, 'Ava', 'Patient', 'ava.patient@example.com', '555-0101', 'PATIENT', 'demo'),
                (2, 'Noah', 'Provider', 'noah.provider@example.com', '555-0102', 'DOCTOR', 'demo'),
                (3, 'Mia', 'Admin', 'mia.admin@example.com', '555-0103', 'ADMIN', 'demo'),
                (4, 'Liam', 'Smith', 'liam.smith@example.com', '555-0104', 'PATIENT', 'demo'),
                (5, 'Olivia', 'Johnson', 'olivia.johnson@example.com', '555-0105', 'PATIENT', 'demo'),
                (6, 'Emma', 'Chen', 'emma.chen@example.com', '555-0106', 'PATIENT', 'demo'),
                (7, 'James', 'Wilson', 'james.wilson@example.com', '555-0107', 'DOCTOR', 'demo'),
                (8, 'Sophia', 'Garcia', 'sophia.garcia@example.com', '555-0108', 'DOCTOR', 'demo'),
                (9, 'Benjamin', 'Lee', 'benjamin.lee@example.com', '555-0109', 'DOCTOR', 'demo')
            """);

        // Patients table - expanded
        jdbcTemplate.update("""
            INSERT IGNORE INTO Patients
                (PatientID, Gender, DateOfBirth, Address)
            VALUES
                (1, 'Female', '1992-04-12', '100 Clinic Way'),
                (4, 'Male', '1985-07-23', '200 Health Ave'),
                (5, 'Female', '1998-11-05', '300 Medical Blvd'),
                (6, 'Female', '1988-02-14', '400 Hospital Lane')
            """);

        // Doctor table - expanded
        jdbcTemplate.update("""
            INSERT IGNORE INTO Doctor
                (DoctorID, LicenseNumber)
            VALUES
                (2, 'CA-MD-1002'),
                (7, 'CA-MD-1007'),
                (8, 'CA-MD-1008'),
                (9, 'CA-MD-1009')
            """);

        jdbcTemplate.update("""
            INSERT IGNORE INTO Admins
                (AdminID, AdminLevel)
            VALUES
                (3, 1)
            """);

        jdbcTemplate.update("""
            INSERT IGNORE INTO Services
                (ServiceID, ServiceName, DurationMinutes, Description)
            VALUES
                (1, 'Primary Care Consultation', '00:30:00', 'General hospital outpatient consultation'),
                (2, 'Follow-up Visit', '00:20:00', 'Follow-up appointment after a prior visit')
            """);

        // Specializations - expanded for new doctors
        jdbcTemplate.update("""
            INSERT IGNORE INTO Specializations
                (DoctorID, SpecializationName, CertificationDate)
            VALUES
                (2, 'Family Medicine', '2020-01-15'),
                (7, 'Cardiology', '2018-06-20'),
                (8, 'Dermatology', '2019-03-10'),
                (9, 'Orthopedics', '2017-09-05')
            """);

        // Availability - expanded with slots for new doctors
        jdbcTemplate.update("""
            INSERT IGNORE INTO Availability
                (SlotID, DoctorID, Date, StartTime, EndTime, ServiceID, Status)
            VALUES
                (1, 2, CURRENT_DATE + INTERVAL 1 DAY, '09:00:00', '09:30:00', 1, 'Available'),
                (2, 2, CURRENT_DATE + INTERVAL 1 DAY, '10:00:00', '10:30:00', 1, 'Available'),
                (3, 2, CURRENT_DATE + INTERVAL 2 DAY, '11:00:00', '11:20:00', 2, 'Available'),
                (4, 7, CURRENT_DATE + INTERVAL 1 DAY, '09:00:00', '09:30:00', 1, 'Available'),
                (5, 7, CURRENT_DATE + INTERVAL 1 DAY, '14:00:00', '14:30:00', 1, 'Available'),
                (6, 8, CURRENT_DATE + INTERVAL 2 DAY, '10:00:00', '10:30:00', 1, 'Available'),
                (7, 8, CURRENT_DATE + INTERVAL 3 DAY, '15:00:00', '15:30:00', 2, 'Available'),
                (8, 9, CURRENT_DATE + INTERVAL 1 DAY, '13:00:00', '13:30:00', 1, 'Available'),
                (9, 9, CURRENT_DATE + INTERVAL 2 DAY, '09:00:00', '09:30:00', 1, 'Available')
            """);
    }
}
