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

        System.out.println(postRequest.getUsername()+ " " + "[File Name] "+file.getName()+ " |[SIZE] "+file.getSize() +" BYTES");

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
    public ResponseEntity<Page<PostResponse>> getTimeline(
            @PathVariable String username,
            @RequestParam int page,
            @RequestParam int size
    ) throws Exception {
        UserModel user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getTimeline(user, page, size));
    }

    @PostMapping("/explore")
    public ResponseEntity<List<PostThumbnailResponse>> getExplore () throws Exception {
        return ResponseEntity.ok(postService.getExplore());
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
        PostModel post = postService.findById(Long.parseLong(postId));
        commentService.addComment(comment, user, post);
        return ResponseEntity.ok("Comment Added");
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> addLike (
            @PathVariable String postId,
            @ModelAttribute LikeRequest likeRequest
    ){
        UserModel user = userService.findById(Long.parseLong(String.valueOf(likeRequest.getUserId())));
        PostModel post = postService.findById(Long.parseLong(String.valueOf(likeRequest.getPostId())));

        likeService.likePost(post, user);

        return ResponseEntity.ok("Like Added on POST ID ::: "+ postId);
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
