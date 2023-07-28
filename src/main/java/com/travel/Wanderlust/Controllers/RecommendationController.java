package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.Location;
import com.travel.Wanderlust.Services.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/recommendation")
    public ResponseEntity<List<Optional<Location>>> getRecommendedCities(@RequestParam("email") String email){
        List<Optional<Location>> recommendedCities = recommendationService.getRecommendedLocations(email);
        return ResponseEntity.ok(recommendedCities);
    }
}
