package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.Image;
import com.travel.Wanderlust.Entities.Location;
import com.travel.Wanderlust.Entities.Post;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.ImageRepository;
import com.travel.Wanderlust.Repositories.LocationRepository;
import com.travel.Wanderlust.Repositories.PostRepository;
import com.travel.Wanderlust.Repositories.UserRepository;
import com.travel.Wanderlust.Services.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;


@RestController
public class ImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;


    @GetMapping("/getImage/{imageSavedName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageSavedName) throws IOException {
        Path imagePath = Paths.get("src/main/resources/images", imageSavedName);

        Resource resource = new UrlResource(imagePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getImages/{username}")
    public List<Image> getImages(@PathVariable String username) throws IOException {
        User user = userService.findByUsername(username);
        Set<Image> images = user.getImagesUploaded();
        List<Image> imagesToReturn = new ArrayList<>();
        for(Image image : images){
            if(image.getFilePath() != null) {
                Path imagePath = Paths.get("src/main/resources/images", image.getFilePath());

                Resource resource = new UrlResource(imagePath.toUri());
                if (resource.exists() && resource.isReadable()) {
                    Image imageWithURI = new Image();
                    imageWithURI.setName(image.getName());
                    imageWithURI.setFilePath("http://localhost:8080/getImage/" + image.getFilePath());
                    imageWithURI.setImage_id(image.getImage_id());
                    imagesToReturn.add(imageWithURI);
                }
            }
        }

        return imagesToReturn;
    }

    @GetMapping("/getAllImages")
    public List<Image> getAllImages() throws IOException {

        List<Image> images = imageRepository.findAll();
        List<Image> imagesToReturn = new ArrayList<>();
        for(Image image : images){
            if(image.getFilePath() != null) {
                Path imagePath = Paths.get("src/main/resources/images", image.getFilePath());

                Resource resource = new UrlResource(imagePath.toUri());
                if (resource.exists() && resource.isReadable()) {
                    Image imageWithURI = new Image();
                    imageWithURI.setName(image.getName());
                    imageWithURI.setLongitude(image.getLongitude());
                    imageWithURI.setLatitude(image.getLatitude());
                    imageWithURI.setFilePath("http://localhost:8080/getImage/" + image.getFilePath());
                    imagesToReturn.add(imageWithURI);
                }
            }
        }
        return imagesToReturn;
    }

    @GetMapping("/getPostDetailsFromImage/{imageId}")
    public ResponseEntity<?> getPostDetailsFromImage(@PathVariable Long imageId) {
        try {
            Image image = imageRepository.findById(imageId).orElse(null);

            if (image == null) {
                return ResponseEntity.notFound().build();
            }
            Post post = postRepository.findByImage(image);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/uploadImage")
    public boolean uploadImage(@RequestParam("email") String email, @RequestParam("image") MultipartFile imageFile, @RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude, @RequestParam("location") String location, @RequestParam("name")String name) {
        try {

            Post post = new Post();
            post.setComments(new ArrayList<>());
            post.setLikes(new ArrayList<>());
            Image image = new Image();

            image.setLatitude(latitude);
            image.setLongitude(longitude);
            User user = userService.findByEmail(email);

            String fileName = imageFile.getOriginalFilename();
            String fileExtension = FilenameUtils.getExtension(fileName);
            String savedFileName = user.getId() + "_" + System.currentTimeMillis() + "." + fileExtension;
            Path savedFilePath = Paths.get("src/main/resources/images", savedFileName);
            Files.copy(imageFile.getInputStream(), savedFilePath, StandardCopyOption.REPLACE_EXISTING);

            image.setUser_id(user);
            image.setName(name);
            image.setFilePath(savedFileName);
            if (user.getImagesUploaded() == null) {
                user.setImagesUploaded(new HashSet<>());
            }

            user.addImage(image);
            Location locationInDb = locationRepository.findByName(location);
            post.setImage(image);
            post.setCreationDate(Date.from(Instant.now()));
            post.setUser(user);
            postRepository.save(post);

            if (locationInDb == null) {
                locationInDb = new Location();
                locationInDb.setName(location);
                locationRepository.save(locationInDb);
            }
            user.addLocation(locationInDb);
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }


}
