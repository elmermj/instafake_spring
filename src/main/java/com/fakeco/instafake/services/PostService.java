package com.fakeco.instafake.services;

import com.fakeco.instafake.dto.PostRequest;
import com.fakeco.instafake.models.PostModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.repos.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostModel createPost(PostRequest request, UserModel user, String uri, String filename) throws IOException {

        PostModel post = PostModel.builder()
                .fileName(filename)
                .caption(request.getCaption())
                .fileUrl(uri)
                .user(user).build();

        postRepository.save(post);

        return post;
    }

    public List<PostModel> getAllPosts() {
        return postRepository.getPostsFromNewest();
    }

    public List<PostModel> getUserPosts(UserModel user) {
        return postRepository.getUserPosts(user.getId());
    }
}
