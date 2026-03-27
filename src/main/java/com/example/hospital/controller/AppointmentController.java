package com.example.hospital.controller;

import com.example.hospital.model.Appointment;
import com.example.hospital.service.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping("/appointments")
    @ResponseBody
    public List<Appointment> viewAppointments() {
        return service.getAppointments();
    }

    @GetMapping("/slots")
    public String viewSlots() {
        return "redirect:/slots.html";
    }

    @GetMapping("/book")
    public String bookForm(){
        return "redirect:/book.html";
    }

    @GetMapping("/confirmation")
    public String confirmation() {
        return "redirect:/confirmation.html";
    }

}
