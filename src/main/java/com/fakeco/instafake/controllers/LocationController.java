package com.fakeco.instafake.controllers;

import com.fakeco.instafake.models.CoordinateConstraints;
import com.fakeco.instafake.models.UserLocation;
import com.fakeco.instafake.services.SimulatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LocationController {

//    @Autowired
//    private SimpMessagingTemplate template;
//
//    @Autowired
//    private SimulatedUserService simulatedUserService;
//
//    @PostMapping("/update-location")
//    public void updateLocation(@RequestBody UserLocation location) {
//        template.convertAndSend("/topic/locations", location);
//    }
//
//    @MessageMapping("/coordinates")
//    public void updateCoordinateConstraints(CoordinateConstraints constraints) {
//        simulatedUserService.updateCoordinateConstraints(constraints);
//    }
    @GetMapping("/")
    public String index() {
        return "client";
    }
}
