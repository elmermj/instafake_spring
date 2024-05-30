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
        UserModel otherUser = userRepository.findById(request.getOtherId()).orElseThrow();
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

    public List<FollowModel> getFollowingCount(Long id){

        return followRepository.getFollowings(id);
    }

    public List<FollowModel> getFollowerCount(Long id){
        return followRepository.getFollowers(id);
    }
}
