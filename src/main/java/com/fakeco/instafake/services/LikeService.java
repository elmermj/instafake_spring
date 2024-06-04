package com.fakeco.instafake.services;

import com.fakeco.instafake.dto.response.UserDTOResponse;
import com.fakeco.instafake.models.LikeModel;
import com.fakeco.instafake.models.PostModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.repos.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public void likePost(PostModel post, UserModel user){

        LikeModel like = LikeModel.builder()
                .user(user).post(post).build();

        likeRepository.save(like);
    }

    public void unlikePost(PostModel post, UserModel user){

        LikeModel like = likeRepository.getLikeByPostIdAndUserId(post.getId(), user.getId());

        likeRepository.delete(like);
    }

    public List<UserDTOResponse> getUsersLikesByPostId(PostModel post){

        return likeRepository.getUsersLikesByPost(post).stream().map(
                UserDTOResponse::new
        ).toList();

    }
}
