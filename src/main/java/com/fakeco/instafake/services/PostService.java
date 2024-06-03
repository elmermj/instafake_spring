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
import com.fakeco.instafake.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    private UserRepository userRepository;

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

    public List<PostResponse> getTimeline(UserModel user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        System.out.println("Fetching timeline for user: " + user.getId() + ", page: " + page + ", size: " + size);
        Page<PostModel> posts = postRepository.getPostsFromUserTimeline(user.getId(), pageable);
        System.out.println("Fetched " + posts.getTotalElements() + " posts for user: " + user.getUsername() + ", page: " + page + ", size: " + size);

        return posts.stream().map(
                postModel -> {
                    List<CommentResponse> comments = commentRepository.getCommentsByPostId(postModel.getId()).stream().map(
                            commentModel -> {
                                UserModel commenter = userRepository.findById(commentModel.getUser().getId()).orElseThrow();
                                return new CommentResponse(
                                        commentModel.getId(),
                                        commentModel.getBody(),
                                        commenter.getUsername(),
                                        commentModel.getCreatedAt(),
                                        commentModel.getCommenterProfPic()
                                );
                            }
                    ).collect(Collectors.toList());

                    int likes = likeRepository.getLikesCountByPostId(postModel.getId());
                    List<LikeModel> likeModels = likeRepository.getLikesByPostId(postModel.getId());

                    List<Long> userIds = likeModels.stream().map(
                            likeModel -> {
                                return likeModel.getUser().getId();
                            }
                    ).toList();

                    return new PostResponse(
                            postModel,
                            comments,
                            likes,
                            postModel.getUser().getUsername(), // Get username directly from the postModel's user
                            postModel.getUser().getProfImageUrl(),
                            userIds// Get profImageUrl directly from the postModel's user
                    );
                }
        ).collect(Collectors.toList());
    }

    public List<PostThumbnailResponse> getExplore(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostModel> posts = postRepository.getExplorePosts(pageable);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        System.out.println(baseUrl);
        return posts.stream().map(post -> {
            String dynamicURL = baseUrl + "/" + post.getFileUrl();
            System.out.println("FILE URL ::: "+dynamicURL + " | POST ID ::: " + post.getId());
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
            post.setFileUrl(dynamicURL);
            UserModel postedBy = userRepository.findUserByPostId(post.getId());
            List<CommentModel> commentModel =  commentRepository.getCommentsByPostId(post.getId());
            List<CommentResponse> commentResponses = commentModel.stream().map(
                    comment -> new CommentResponse(
                            comment.getId(),
                            comment.getBody(),
                            comment.getUser().getUsername(),
                            comment.getCreatedAt(),
                            comment.getCommenterProfPic()
                    )).collect(Collectors.toList());
            int likes = likeRepository.getLikesCountByPostId(post.getId());
            List<LikeModel> likeModels = likeRepository.getLikesByPostId(post.getId());

            List<Long> userIds = likeModels.stream().map(
                    likeModel -> {
                        return likeModel.getUser().getId();
                    }
            ).toList();

            return new PostResponse(post, commentResponses, likes, postedBy.getUsername(), postedBy.getProfImageUrl(), userIds);
        }).collect(Collectors.toList());
    }

    public PostResponse refreshPost(String id){
        Long postId = Long.parseLong(id);
        System.out.println("refreshPost ::: 1");
        PostModel post = postRepository.findById(postId).orElseThrow();
        System.out.println("refreshPost ::: 2");
        UserModel postedBy = userRepository.findUserByPostId(post.getId());
        System.out.println("refreshPost ::: 3");
        List<CommentModel> commentModel =  commentRepository.getCommentsByPostId(post.getId());
        System.out.println("refreshPost ::: 4");
        List<CommentResponse> commentResponses = commentModel.stream().map(
                comment -> new CommentResponse(
                        comment.getId(),
                        comment.getBody(),
                        comment.getUser().getUsername(),
                        comment.getCreatedAt(),
                        comment.getCommenterProfPic()
                )).collect(Collectors.toList());
        System.out.println("refreshPost ::: 5");
        int likes = likeRepository.getLikesCountByPostId(post.getId());
        System.out.println("refreshPost ::: 6");
        List<LikeModel> likeModels = likeRepository.getLikesByPostId(post.getId());
        System.out.println("refreshPost ::: 7");

        List<Long> userIds = likeModels.stream().map(
                likeModel -> {
                    return likeModel.getUser().getId();
                }
        ).toList();
        System.out.println("refreshPost ::: 8");
        return new PostResponse(post, commentResponses, likes, postedBy.getUsername(), postedBy.getProfImageUrl(), userIds);
    }

}
