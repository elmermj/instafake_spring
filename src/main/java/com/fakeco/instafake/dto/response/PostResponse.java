package com.fakeco.instafake.dto.response;

import com.fakeco.instafake.models.PostModel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class PostResponse {
    private Long id;
    private String fileUrl;
    private String fileName;
    private String caption;
    private String creatorUsername;
    private String creatorProfPicUrl;
    private Timestamp createdAt;
    private Long userId;
    private String fileContent;
    private List<CommentResponse> comments;
    private List<Long> userIdsLike;
    private int likes;

    public PostResponse(PostModel post, List<CommentResponse> comments, int likes, String username, String creatorProfPicUrl, List<Long> userIdsLike) {
        this.id = post.getId();
        this.fileUrl = post.getFileUrl();
        this.caption = post.getCaption();
        this.creatorUsername = username;
        this.createdAt = post.getCreatedAt();
        this.userId = post.getUser().getId();
        this.fileName = post.getFileName();
        this.comments = comments;
        this.userIdsLike = userIdsLike;
        this.likes = likes;
        this.creatorProfPicUrl = creatorProfPicUrl;
    }
}