package com.fakeco.instafake.controllers;

import com.fakeco.instafake.dto.CommentRequest;
import com.fakeco.instafake.dto.LikeRequest;
import com.fakeco.instafake.dto.PostResponse;
import com.fakeco.instafake.dto.PostRequest;
import com.fakeco.instafake.models.PostModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.services.CommentService;
import com.fakeco.instafake.services.FileProcessingService;
import com.fakeco.instafake.services.PostService;
import com.fakeco.instafake.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    FileProcessingService fileProcessingService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute PostRequest postRequest
    ) throws Exception {

        System.out.println(postRequest.getUsername()+ " " + "[File Name] "+file.getName()+ " |[SIZE] "+file.getSize() +" BYTES");

        UserModel user = userService.findByUsername(postRequest.getUsername());

        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authenticated");
        }else{
            String filename = user.getUsername() +"_"
                    + LocalDateTime.now().getYear()
                    + LocalDateTime.now().getMonthValue()
                    + LocalDateTime.now().getDayOfMonth()
                    + LocalDateTime.now().getHour()
                    + LocalDateTime.now().getMinute()
                    + LocalDateTime.now().getSecond();

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
    public ResponseEntity<List<PostResponse>> getAllPosts (
            @PathVariable String username
    ) throws Exception {
        UserModel user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PostMapping("/{username}/getUserPosts")
    public ResponseEntity<List<PostResponse>> getUserPosts (
            @PathVariable String username
    ) throws Exception {
        UserModel user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getUserPosts(user));
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment (
            @PathVariable String postId,
            @RequestBody CommentRequest requestBody
    ) throws Exception {
        String username = requestBody.getUsername();
        String comment = requestBody.getComment();

        System.out.println("[USERNAME] "+username+ " " + "[POST ID] "+postId+" |[COMMENT] "+comment);
        UserModel user = userService.findByUsername(username);
        PostModel post = postService.getPost(Long.parseLong(postId));
        commentService.addComment(comment, user, post);
        return ResponseEntity.ok("Comment Added");
    }

    @PostMapping("/{postId/like")
    public ResponseEntity<?> addLike (
            @PathVariable String postId,
            @ModelAttribute LikeRequest likeRequest
    ){
        Long userId = Long.parseLong(String.valueOf(likeRequest.getUserId()));

        return ResponseEntity.ok("Like Added on POST ID ::: "+ postId);
    }

}
