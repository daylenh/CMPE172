package com.example.hospital.service;

import com.example.hospital.model.Appointment;
import com.example.hospital.model.BookingResult;
import com.example.hospital.model.NotificationRequest;
import com.example.hospital.model.NotificationResponse;
import com.example.hospital.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository repository;
    private final AppointmentTransactionService transactionService;
    private final NotificationGateway notificationGateway;
    private final MonitoringService monitoringService;

    public AppointmentService(AppointmentRepository repository,
                              AppointmentTransactionService transactionService,
                              NotificationGateway notificationGateway,
                              MonitoringService monitoringService) {
        this.repository = repository;
        this.transactionService = transactionService;
        this.notificationGateway = notificationGateway;
        this.monitoringService = monitoringService;
    }

    public List<Appointment> getAppointments() {
        return repository.findAll();
    }

    public Optional<Appointment> getAppointment(int appointmentId) {
        return repository.findById(appointmentId);
    }

    public BookingResult bookAppointment(int patientId, int doctorId, int slotId, int serviceId) {
        long startedAt = System.nanoTime();
        log.info("Booking request received for patient {}, doctor {}, slot {}, service {}",
                patientId, doctorId, slotId, serviceId);

        try {
            Optional<Appointment> appointment = transactionService.bookAppointment(
                    patientId, doctorId, slotId, serviceId);
            if (appointment.isEmpty()) {
                log.warn("Booking rejected for patient {} and slot {} because the slot is unavailable",
                        patientId, slotId);
                monitoringService.recordBooking(false, startedAt);
                return BookingResult.bookingFailed();
            }

            log.info("Appointment {} created successfully for patient {} and slot {}",
                    appointment.get().getAppointmentID(), patientId, slotId);

            NotificationResponse response = notifyAppointmentBooked(appointment.get());
            boolean notificationSent = "SENT".equalsIgnoreCase(response.status());
            if (!notificationSent) {
                log.warn("Notification delivery failed for appointment {} with message: {}",
                        appointment.get().getAppointmentID(), response.message());
            }

            BookingResult result = new BookingResult(
                    true,
                    appointment.get(),
                    notificationSent,
                    response.message()
            );
            monitoringService.recordBooking(true, startedAt);
            return result;
        } catch (RuntimeException ex) {
            log.error("Booking operation failed for patient {} and slot {}", patientId, slotId, ex);
            monitoringService.recordBooking(false, startedAt);
            throw ex;
        }
    }

    public NotificationResponse notifyAppointmentBooked(Appointment appointment) {
        NotificationRequest request = new NotificationRequest(
                appointment.getAppointmentID(),
                appointment.getPatientID(),
                appointment.getDoctorID(),
                appointment.getSlotID(),
                appointment.getServiceID(),
                "EMAIL"
        );
        log.info("Calling notification service for appointment {}", appointment.getAppointmentID());
        return notificationGateway.sendBookingConfirmation(request);
    }

    public Optional<Appointment> cancelAppointment(int appointmentId) {
        long startedAt = System.nanoTime();
        log.info("Cancel request received for appointment {}", appointmentId);

        Optional<Appointment> appointment = transactionService.cancelAppointment(appointmentId);

        if (appointment.isPresent()) {
            log.info("Appointment {} canceled and slot {} released",
                    appointmentId, appointment.get().getSlotID());
        } else {
            log.warn("Cancel request rejected because appointment {} was not found or not booked", appointmentId);
        }

        monitoringService.recordCancellation(appointment.isPresent(), startedAt);
        return appointment;
    }
}
