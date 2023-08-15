package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.*;
import com.travel.Wanderlust.Repositories.*;
import com.travel.Wanderlust.Services.NotificationService;
import com.travel.Wanderlust.Services.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/register")
    public Map<String, String> saveUser(@RequestBody User user) {
        System.out.println("We are creating a new user now");
        return userService.save(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        System.out.println("We are getting all users");
        return userService.findAll();
    }

    @GetMapping("/users/{email}")
    public User getUsers(@PathVariable String email) {
        System.out.println(email);
        if(email.contains("@")){
            System.out.println("Getting user by email");
            return userService.findByEmail(email);
        }else{
            System.out.println("Getting user by username");
            return userService.findByUsername(email);
        }
    }

    @PostMapping("/uploadImage")
    public boolean uploadImage(@RequestParam("email") String email, @RequestParam("image") MultipartFile imageFile, @RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude,@RequestParam("location") String location, @RequestParam("name")String name) {
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
                // Location does not exist, create a new one
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

    @GetMapping("/getLatestPosts")
    public List<Post> getPosts(
            @RequestParam int page,
            @RequestParam int pageSize) {
        // Calculate the start and end dates for the last month
        LocalDate currentDate = LocalDate.now();
        LocalDate lastMonthStart = currentDate.withDayOfMonth(1);
        LocalDate lastMonthEnd = currentDate.withDayOfMonth(lastMonthStart.lengthOfMonth());

        // Convert LocalDate to Date
        Date startDate = Date.from(lastMonthStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastMonthEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());
        System.out.println("Start date" + startDate + "   End date: " + endDate);

        // Calculate the offset for pagination
        Sort sortByCreationDateDesc = Sort.by(Sort.Direction.DESC, "creationDate");
        Pageable pageable = PageRequest.of(page - 1, pageSize, sortByCreationDateDesc);
        return postRepository.findAllByCreationDateBetween(startDate, endDate, pageable);
    }

    @GetMapping("/getPostsOfUser/{id}")
    public List<Post> getPostsOfUser(
           @PathVariable Long id) {
        return postRepository.findByUserId(id);
    }

    @GetMapping("/getFollowingPosts")
    public ResponseEntity<?> getFollowingPosts(
            @RequestParam String userEmail,
            @RequestParam int page,
            @RequestParam int pageSize) {
        try {
            User user = userRepository.findByEmail(userEmail);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Set<User> following = user.getFollowing();
            Page<Post> followingPostsPage = postRepository.findByUserIn(following, PageRequest.of(page - 1, pageSize));

            return ResponseEntity.ok(followingPostsPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/like/{postId}")
    public ResponseEntity<?> likeOrUnlikePost(@PathVariable("postId") Long postId, @RequestParam("email") String email) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                User user = userRepository.findByEmail(email);

                // Check if the user has already liked the post
                Like existingLike = likeRepository.findByPostAndUser(post, user);
                if (existingLike != null) {
                    // User has already liked the post, so remove the like
                    post.removeLike(existingLike);
                    likeRepository.delete(existingLike);
                } else {
                    // User has not liked the post, so add the like
                    post.addLike(new Like(post, user));

                    // Create like notification
                    notificationService.createLikeNotification(post.getUser(), user, postId);
                }

                postRepository.save(post);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(post);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @PostMapping("/comment/{postId}")
    public ResponseEntity<?> addComment(@PathVariable("postId") Long postId, @RequestParam("email") String email, @RequestBody Map<String, String> requestBody) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                User user = userRepository.findByEmail(email);

                String commentText = requestBody.get("text");
                Comment comment = new Comment(post, user, commentText);
                post.addComment(comment);
                postRepository.save(post);

                // Create comment notification
                notificationService.createCommentNotification(post.getUser(), user, postId);

                return ResponseEntity.status(HttpStatus.ACCEPTED).body(post);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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



    @GetMapping("/getImage/{imageSavedName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageSavedName) throws IOException {
        Path imagePath = Paths.get("src/main/resources/images", imageSavedName);

        Resource resource = new UrlResource(imagePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Change the content type based on your image type
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
    public List<Image> getImages() throws IOException {

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

    @PostMapping("/user/locations")
    public ResponseEntity<?> addLocationsToUser(@RequestParam("email") String email, @RequestParam List<String> locations) {
        User user = userService.findByEmail(email);

        if (user == null) {
            // User not found
            return ResponseEntity.notFound().build();
        }

        List<Location> visitedLocations = new ArrayList<>();

        for (String locationName : locations) {
            Location location = locationRepository.findByName(locationName);

            if (location == null) {
                // Location does not exist, create a new one
                location = new Location();
                location.setName(locationName);
                locationRepository.save(location);
            }

            visitedLocations.add(location);
        }

        // Add visited locations to the user
        user.getVisitedLocations().addAll(visitedLocations);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/search")
    public List<User> searchUsers(@RequestParam("username") String username) {
        // Implement the logic to search for users based on the provided username
        List<User> users = userService.searchUsersByUsername(username);
        return users;
    }

    // Controller method to handle following a user
    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestParam String userEmailToFollow, @RequestParam String currentUserEmail) {
        try {
            User userToFollow = userRepository.findByEmail(userEmailToFollow);
            User currentUser = userRepository.findByEmail(currentUserEmail);

            if (currentUser != null && userToFollow != null) {
                currentUser.addFollowing(userToFollow);
                userToFollow.addFollower(currentUser);
                userRepository.save(currentUser);
                userRepository.save(userToFollow);

                // Create follow notification
                notificationService.createFollowNotification(userToFollow, currentUser);

                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/getFollowing/{currentUserUsername}")
    public ResponseEntity<?> getFollowing(@PathVariable String currentUserUsername) {
        // Retrieve the current user and the user to follow from your repository
        User currentUser = userRepository.findByUsername(currentUserUsername);
        System.out.println("This user is following: " + currentUser.getFollowing());
        return ResponseEntity.ok().body(currentUser.getFollowing());

    }



    @GetMapping("/getFollowers/{currentUserUsername}")
    public ResponseEntity<?> getFollowers(@PathVariable String currentUserUsername) {
        // Retrieve the current user and the user to follow from your repository
        User currentUser = userRepository.findByUsername(currentUserUsername);
        System.out.println("This user is following: " + currentUser.getFollowing());
        return ResponseEntity.ok().body(currentUser.getFollowers());

    }
    @GetMapping("getNotifications/{username}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            // Return an empty list or appropriate response if user is not found
            return Collections.emptyList();
        }
        return notificationService.getUnreadNotifications(user);
    }

    // Controller method to handle unfollowing a user
    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestParam String userEmailToUnfollow, @RequestParam String currentUserEmail) {
        // Retrieve the current user and the user to unfollow from your repository
        User currentUser = userRepository.findByEmail(currentUserEmail);
        User userToUnfollow = userRepository.findByEmail(userEmailToUnfollow);

        if (currentUser != null && userToUnfollow != null) {
            currentUser.unfollow(userToUnfollow);
            userRepository.save(currentUser);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }



}
