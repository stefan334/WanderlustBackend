package com.travel.Wanderlust.Services;

import com.travel.Wanderlust.Repositories.ImageRepository;
import com.travel.Wanderlust.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;


}
