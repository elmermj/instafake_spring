package com.fakeco.instafake.controllers;

import com.fakeco.instafake.dto.request.CommentRequest;
import com.fakeco.instafake.dto.request.LikeRequest;
import com.fakeco.instafake.dto.response.PostResponse;
import com.fakeco.instafake.dto.request.PostRequest;
import com.fakeco.instafake.dto.response.PostThumbnailResponse;
import com.fakeco.instafake.dto.response.UserDTOResponse;
import com.fakeco.instafake.models.PostModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    FileProcessingService fileProcessingService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute PostRequest postRequest
    ) throws Exception {

        logger.debug("{} [File Name] {} |[SIZE] {} BYTES", postRequest.getUsername(), file.getName(), file.getSize());

        UserModel user = userService.findByUsername(postRequest.getUsername());

        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authenticated");
        }else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String formattedDateTime = LocalDateTime.now().format(formatter);
            String filename = user.getUsername() +"_"+ formattedDateTime;

            ResponseEntity<String> uri = fileProcessingService.uploadFile(filename, file);

            if(Objects.equals(uri.getBody(), "ERROR")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR");
            }

            PostModel post = postService.createPost(postRequest, user, uri.getBody(), filename);

            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/downloadFile/")
                    .path(post.getFileName())
                    .toUriString();

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    "POST CREATED : [File Name] "+post.getFileName()+" |[URI] "+downloadUri+" |[SIZE] "+file.getSize()
            );
        }
    }

    @PostMapping("/{username}/timeline")
    public ResponseEntity<List<PostResponse>> getTimeline(
            @PathVariable String username,
            @RequestParam int page,
            @RequestParam int size
    ) throws Exception {
        logger.debug("TIMELINE REQUEST FROM {} | PAGE {} | SIZE {}", username, page, size);
        UserModel user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getTimeline(user, page, size));
    }

    @PostMapping("/admin/timeline")
    public ResponseEntity<List<PostResponse>> getTimeline(
            @RequestParam int page,
            @RequestParam int size
    ) throws Exception {
        return ResponseEntity.ok(postService.getAdminTimeline(page, size));
    }

    @PostMapping("/explore")
    public ResponseEntity<List<PostThumbnailResponse>> getExplore (
            @RequestParam int page,
            @RequestParam int size
    ) throws Exception {
        return ResponseEntity.ok(postService.getExplore(page, size));
    }

    @PostMapping("/{username}/getUserPosts")
    public ResponseEntity<List<PostResponse>> getUserPosts (
            @PathVariable String username
    ) throws Exception {
        UserModel user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getUserPosts(user));
    }

    @PostMapping("/{postId}/refresh")
    public ResponseEntity<PostResponse> refreshPost(
            @PathVariable String postId
    ) throws Exception {
        return ResponseEntity.ok(postService.refreshPost(postId));
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<PostResponse> addComment (
            @PathVariable String postId,
            @RequestBody CommentRequest requestBody
    ) throws Exception {
        String username = requestBody.getUsername();
        String comment = requestBody.getComment();

        System.out.println("[USERNAME] "+username+ " " + "[POST ID] "+postId+" |[COMMENT] "+requestBody.getComment());
        UserModel user = userService.findByUsername(username);
        System.out.println("[USERNAME] "+user.getUsername()+ " " + "[POST ID] "+postId+" |[COMMENT] "+comment);
        PostModel post = postService.findById(Long.parseLong(postId));
        commentService.addComment(comment, user, post, user.getProfImageUrl());
        PostResponse postResponse = postService.refreshPost(postId);
        return ResponseEntity.ok(postResponse);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> addLike (
            @PathVariable String postId,
            @RequestParam String userId
    ){

        Long userIdParsed = Long.parseLong(userId);
        Long postIdParsed = Long.parseLong(postId);

        try {
            UserModel user = userService.findById(userIdParsed);
            PostModel post = postService.findById(postIdParsed);

            likeService.likePost(post, user);

            return ResponseEntity.ok("Like Added on POST ID ::: " + postId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid User ID or Post ID");
        }
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<?> removeLike (
            @PathVariable String postId,
            @RequestParam String userId
    ){

        Long userIdParsed = Long.parseLong(userId);
        Long postIdParsed = Long.parseLong(postId);

        try {
            UserModel user = userService.findById(userIdParsed);
            PostModel post = postService.findById(postIdParsed);

            likeService.unlikePost(post, user);

            return ResponseEntity.ok("Like Removed on POST ID ::: " + postId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid User ID or Post ID");
        }
    }


    @PostMapping("/{postId}/like/users")
    public ResponseEntity<List<UserDTOResponse>> getUsersLikes (
            @PathVariable String postId
    ){
        PostModel post = postService.findById(Long.parseLong(String.valueOf(postId)));

        List<UserDTOResponse> users = likeService.getUsersLikesByPostId(post);

        return ResponseEntity.ok(users);
    }

}
