package com.example.hospital.controller;

import com.example.hospital.service.AvailabilityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.sql.Time;

@Controller
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/slots")
    public String getSlots(Model model) {
        model.addAttribute("slots", availabilityService.getAllSlots());
        return "slots";
    }

    @GetMapping("/provider/availability")
    public String providerAvailability(Model model) {
        model.addAttribute("slots", availabilityService.getAllSlots());
        return "slots";
    }

    @PostMapping("/provider/availability")
    public String createAvailability(@RequestParam int doctorId,
                                     @RequestParam String date,
                                     @RequestParam String startTime,
                                     @RequestParam String endTime,
                                     @RequestParam int serviceId) {
        availabilityService.createSlot(doctorId, Date.valueOf(date), parseTime(startTime), parseTime(endTime), serviceId);
        return "redirect:/slots";
    }

    private Time parseTime(String value) {
        return Time.valueOf(value.length() == 5 ? value + ":00" : value);
    }
}
