package com.fakeco.instafake.services;

import com.fakeco.instafake.dto.response.CommentResponse;
import com.fakeco.instafake.dto.response.PostResponse;
import com.fakeco.instafake.dto.request.PostRequest;
import com.fakeco.instafake.dto.response.PostThumbnailResponse;
import com.fakeco.instafake.models.CommentModel;
import com.fakeco.instafake.models.LikeModel;
import com.fakeco.instafake.models.PostModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.repos.CommentRepository;
import com.fakeco.instafake.repos.LikeRepository;
import com.fakeco.instafake.repos.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FileProcessingService fileProcessingService;

    @Autowired
    private LikeRepository likeRepository;

    @Value("${file.upload-dir}")
    private String basePath;

    public PostModel createPost(PostRequest request, UserModel user, String uri, String filename) throws IOException {

        PostModel post = PostModel.builder()
                .fileName(filename)
                .caption(request.getCaption())
                .fileUrl(uri)
                .user(user).build();

        postRepository.save(post);

        return post;
    }

    public PostModel findById(Long postId) {
        return postRepository.findById(postId).orElseThrow();
    }

    public List<PostResponse> getTimeline() {
        List<PostModel> posts = postRepository.getPostsFromNewest();
        return getPostsResponse(posts);
    }

    public List<PostThumbnailResponse> getExplore() {
        List<PostModel> posts = postRepository.getPostsFromNewest();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return posts.stream().map(post -> {
            String dynamicURL = baseUrl + "/" + post.getFileUrl();
            System.out.println("FILE URL ::: "+dynamicURL);
            return new PostThumbnailResponse(post);
        }).collect(Collectors.toList());
    }

    public List<PostResponse> getUserPosts(UserModel user) {
        List<PostModel> posts = postRepository.getUserPosts(user.getId());
        return getPostsResponse(posts);
    }

    public List<PostThumbnailResponse> getUserPostsThumbnails(UserModel user) {
        List<PostModel> posts = postRepository.getUserPosts(user.getId());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return posts.stream().map(post -> {
            String dynamicURL = baseUrl + "/" + post.getFileUrl();
            System.out.println("FILE URL ::: "+dynamicURL);
            return new PostThumbnailResponse(post);
        }).collect(Collectors.toList());
    }

    public List<PostResponse> getPostsResponse(List<PostModel> posts) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return posts.stream().map(post -> {
            String dynamicURL = baseUrl + "/" + post.getFileUrl();
            System.out.println("FILE URL ::: "+dynamicURL);
            List<CommentModel> commentModel =  commentRepository.getCommentsByPostId(post.getId());
            List<CommentResponse> commentResponses = commentModel.stream().map(
                    comment -> new CommentResponse(
                            comment.getId(),
                            comment.getBody(),
                            comment.getUser().getUsername(),
                            comment.getCreatedAt()
                    )).collect(Collectors.toList());
            int likes = likeRepository.getLikesCountByPostId(post.getId());
            return new PostResponse(post, commentResponses, likes);
        }).collect(Collectors.toList());
    }

}
