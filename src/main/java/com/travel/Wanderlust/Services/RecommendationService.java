package com.travel.Wanderlust.Services;

import com.travel.Wanderlust.Entities.Location;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.LocationRepository;
import com.travel.Wanderlust.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;
@Service
public class RecommendationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;

    public List<Optional<Location>> getRecommendedLocations(String username) {

        try {
            User user = userRepository.findByUsername(username);
            ProcessBuilder processBuilder = new ProcessBuilder("C:/Python310/python.exe", "C:/Uni/Licenta/Backend/Wanderlust/src/main/resources/python/script.py", user.getId().toString());

            Process process = null;

            try {
                process = processBuilder.start();
            }catch (Exception e){
                System.out.println(e);
            }
            assert process != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            System.out.println(output);

            String[] recommendedCitiesArray = output.toString().replaceAll("[\\[\\]\\s]", "").split(",");
            List<Optional<Location>> locations = new LinkedList<>();

            for (String s : recommendedCitiesArray) {
                System.out.println(Long.valueOf(Integer.parseInt(s)));
                locations.add(locationRepository.findById((long) Integer.parseInt(s)));
            }
            System.out.println(locations);
            return locations;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

}
