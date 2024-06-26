package com.fakeco.instafake.services;

import com.fakeco.instafake.dto.request.FollowRequest;
import com.fakeco.instafake.models.FollowModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.repos.FollowRepository;
import com.fakeco.instafake.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    public void followUser(FollowRequest request){
        UserModel user = userRepository.findById(request.getId()).orElseThrow();
        System.out.println("USER NAME ::: "+user.getUsername());
        UserModel otherUser = userRepository.findById(request.getOtherId()).orElseThrow();
        System.out.println("OTHER USER NAME ::: "+otherUser.getUsername());
        FollowModel follow = FollowModel.builder()
                .user(user)
                .otherUser(otherUser)
                .build();
        followRepository.save(follow);
    }

    public void unfollowUser(FollowRequest request){
        UserModel user = userRepository.findById(request.getId()).orElseThrow();
        followRepository.deleteByUserId(user.getId());
    }

    public void removeFollower(FollowRequest request){
        UserModel user = userRepository.findById(request.getId()).orElseThrow();
        followRepository.removeFollowerById(user.getId());
    }

    public List<Long> getFollowerIds(Long id){
        return followRepository.getListOfFollowers(id);
    }

    public List<Long> getFollowingIds(Long id){
        return followRepository.getListOfFollowing(id);
    }
}

