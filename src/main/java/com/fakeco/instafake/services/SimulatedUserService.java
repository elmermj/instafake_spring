package com.fakeco.instafake.services;

import com.fakeco.instafake.models.CoordinateConstraints;
import com.fakeco.instafake.models.UserLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SimulatedUserService {

//    @Autowired
//    private SimpMessagingTemplate template;

    private Map<String, UserLocation> users = new HashMap<>();
    private Random random = new Random();
    private CoordinateConstraints currentConstraints;

    public SimulatedUserService() {
        // Initialize 10 users with random starting locations
        for (int i = 1; i <= 10; i++) {
            String userId = "user" + i;
            users.put(userId, new UserLocation(userId, randomLat(), randomLng()));
        }
    }

    @Scheduled(fixedRate = 5000) // Update every 5 seconds
    public void simulateUserMovement() {
        for (UserLocation user : users.values()) {
            user.setLatitude(user.getLatitude() + randomMovement());
            user.setLongitude(user.getLongitude() + randomMovement());
        }

        if (currentConstraints != null) {
            sendFilteredLocations(currentConstraints);
        }
    }

    public void updateCoordinateConstraints(CoordinateConstraints constraints) {
        this.currentConstraints = constraints;
        sendFilteredLocations(constraints);
    }

    private void sendFilteredLocations(CoordinateConstraints constraints) {
        var filteredUsers = users.values().stream()
                .filter(user -> isWithinConstraints(user, constraints))
                .collect(Collectors.toList());

//        filteredUsers.forEach(user -> template.convertAndSend("/topic/locations", user));
    }

    private boolean isWithinConstraints(UserLocation user, CoordinateConstraints constraints) {
        return user.getLatitude() <= constraints.getNorthEast().getLat()
                && user.getLatitude() >= constraints.getSouthWest().getLat()
                && user.getLongitude() <= constraints.getNorthEast().getLng()
                && user.getLongitude() >= constraints.getSouthWest().getLng();
    }

    private double randomLat() {
        return 37.7749 + (random.nextDouble() - 0.5) * 0.01; // Near San Francisco
    }

    private double randomLng() {
        return -122.4194 + (random.nextDouble() - 0.5) * 0.01; // Near San Francisco
    }

    private double randomMovement() {
        return (random.nextDouble() - 0.5) * 0.001; // Small random movement
    }
}

