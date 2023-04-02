package com.travel.Wanderlust.Repositories;

import com.travel.Wanderlust.Entities.Image;
import com.travel.Wanderlust.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}
