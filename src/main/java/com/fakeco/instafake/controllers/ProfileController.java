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
        System.out.println("MY ID ::: "+request.getId());
        System.out.println("OTHER ID ::: "+request.getOtherId());
        followService.followUser(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestBody FollowRequest request){
        followService.unfollowUser(request);
        return new ResponseEntity<>((HttpStatus.ACCEPTED));
    }

    @PostMapping("/removeFollower")
    public ResponseEntity<?> removeFollower(@RequestBody FollowRequest request){
        followService.removeFollower(request);
        return new ResponseEntity<>((HttpStatus.ACCEPTED));
    }

    @PostMapping("/{username}")
    public ResponseEntity<ProfileResponse> getProfileData(
            @PathVariable String username
    ) throws Exception {
        UserModel user = userService.findByUsername(username);
        UserDTOResponse userDTOResponse = new UserDTOResponse(user);
        System.out.println("USER ID ::: " + userDTOResponse.getId());
        List<PostThumbnailResponse> postThumbnailResponseList = postService.getUserPostsThumbnails(user);
        List<Long> followers = followService.getFollowerIds(user.getId());
        System.out.println(followers);
        List<Long> followings = followService.getFollowingIds(user.getId());

        ProfileResponse profileResponse = new ProfileResponse(
                userDTOResponse,
                postThumbnailResponseList,
                followers,
                followings
        );
        return ResponseEntity.ok(profileResponse);
    }
}
