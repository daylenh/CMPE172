package com.example.hospital.model;

public class Appointment {
    private String doctorName;
    private String date;
    private String time;

    public Appointment(String doctorName, String date, String time){
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}