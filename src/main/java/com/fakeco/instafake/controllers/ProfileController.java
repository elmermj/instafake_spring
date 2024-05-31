package com.fakeco.instafake.controllers;

import com.fakeco.instafake.dto.request.FollowRequest;
import com.fakeco.instafake.dto.response.PostThumbnailResponse;
import com.fakeco.instafake.dto.response.ProfileResponse;
import com.fakeco.instafake.dto.response.UserDTOResponse;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.services.FollowService;
import com.fakeco.instafake.services.PostService;
import com.fakeco.instafake.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private PostService postService;

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestBody FollowRequest request){
        followService.followUser(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestBody FollowRequest request){
        followService.unfollowUser(request);
        return new ResponseEntity<>((HttpStatus.ACCEPTED));
    }

    @PostMapping("/{username}")
    public ResponseEntity<ProfileResponse> getProfileData(
            @PathVariable String username
    ) throws Exception {
        UserModel user = userService.findByUsername(username);
        UserDTOResponse userDTOResponse = new UserDTOResponse(user);
        List<PostThumbnailResponse> postThumbnailResponseList = postService.getUserPostsThumbnails(user);
        int followers = followService.getFollowerCount(user.getId()).stream().toList().size();
        int followings = followService.getFollowingCount(user.getId()).stream().toList().size();

        ProfileResponse profileResponse = new ProfileResponse(
                userDTOResponse,
                postThumbnailResponseList,
                followers,
                followings
        );
        return ResponseEntity.ok(profileResponse);
    }
}