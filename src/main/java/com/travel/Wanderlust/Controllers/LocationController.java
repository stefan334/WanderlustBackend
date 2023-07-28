package com.travel.Wanderlust.Controllers;


import com.travel.Wanderlust.Entities.Location;
import com.travel.Wanderlust.Repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocationController {
    @Autowired
    private LocationRepository locationRepository;



    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return ResponseEntity.ok(locations);
    }

    @PostMapping("/locations")
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        Location createdLocation = locationRepository.save(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
    }

    // Add more endpoints for updating, deleting, and retrieving specific locations as needed

}