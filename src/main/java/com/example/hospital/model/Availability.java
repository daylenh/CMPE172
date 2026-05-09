package com.example.hospital.model;

import java.sql.Date;
import java.sql.Time;

public class Availability {

    private int slotId;
    private int doctorId;
    private Date date;
    private Time startTime;
    private Time endTime;
    private int serviceId;
    private String status;

    public Availability(int slotId, int doctorId, Date date, Time startTime, Time endTime, int serviceId, String status) {
        this.slotId = slotId;
        this.doctorId = doctorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceId = serviceId;
        this.status = status;
    }

    public int getSlotId() { return slotId; }
    public int getDoctorId() { return doctorId; }
    public Date getDate() { return date; }
    public Time getStartTime() { return startTime; }
    public Time getEndTime() { return endTime; }
    public int getServiceId() { return serviceId; }
    public String getStatus() { return status; }
}