package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.Comment;
import com.travel.Wanderlust.Entities.Like;
import com.travel.Wanderlust.Entities.Post;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.LikeRepository;
import com.travel.Wanderlust.Repositories.PostRepository;
import com.travel.Wanderlust.Repositories.UserRepository;
import com.travel.Wanderlust.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/getLatestPosts")
    public List<Post> getPosts(
            @RequestParam int page,
            @RequestParam int pageSize) {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastMonthStart = currentDate.withDayOfMonth(1);
        LocalDate lastMonthEnd = currentDate.withDayOfMonth(lastMonthStart.lengthOfMonth());

        Date startDate = Date.from(lastMonthStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastMonthEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());

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
            Sort sortByCreationDateDesc = Sort.by(Sort.Direction.DESC, "creationDate");
            Pageable pageable = PageRequest.of(page - 1, pageSize, sortByCreationDateDesc);


            return ResponseEntity.ok(postRepository.findByUserIn(following, pageable));
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

                Like existingLike = likeRepository.findByPostAndUser(post, user);
                if (existingLike != null) {
                    post.removeLike(existingLike);
                    likeRepository.delete(existingLike);
                } else {
                    post.addLike(new Like(post, user));
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
                notificationService.createCommentNotification(post.getUser(), user, postId);

                return ResponseEntity.status(HttpStatus.ACCEPTED).body(post);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

}
