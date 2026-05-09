package com.example.hospital.model;

public class Appointment {
    private int appointmentID;
    private int patientID;
    private int doctorID;
    private int slotID;
    private int serviceID;
    private String status;

    public Appointment(int appointmentID, int patientID, int doctorID,
                       int slotID, int serviceID, String status) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.slotID = slotID;
        this.serviceID = serviceID;
        this.status = status;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public int getPatientID() {
        return patientID;
    }

    public int getDoctorID() {
        return doctorID;
    }

    public int getSlotID(){
        return slotID;
    }

    public int getServiceID(){
        return serviceID;
    }

    public String getStatus() { return status; }

    public void setAppointmentID(int appointmentId) {
        this.appointmentID = appointmentId;
    }

    public void setPatientId(int patientId) {
        this.patientID = patientId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorID = doctorId;
    }

    public void setSlotId(int slotId) {
        this.slotID = slotId;
    }

    public void setServiceId(int serviceId) {
        this.serviceID = serviceId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}