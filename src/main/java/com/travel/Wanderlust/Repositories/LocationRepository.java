package com.travel.Wanderlust.Repositories;

import com.travel.Wanderlust.Entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    public Location findByName(String name);
}
