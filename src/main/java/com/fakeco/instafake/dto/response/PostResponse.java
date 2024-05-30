package com.fakeco.instafake.dto.response;

import com.fakeco.instafake.models.LikeModel;
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
    private Timestamp createdAt;
    private Long userId;
    private String fileContent;  // Base64 encoded file content
    private List<CommentResponse> comments;
    private int likes;

    public PostResponse(PostModel post, List<CommentResponse> comments, int likes) {
        this.id = post.getId();
        this.fileUrl = post.getFileUrl();
        this.caption = post.getCaption();
        this.createdAt = post.getCreatedAt();
        this.userId = post.getUser().getId();
        this.fileName = post.getFileName();
        this.comments = comments;
        this.likes = likes;
//        this.fileContent = Base64.getEncoder().encodeToString(fileBytes);
    }
}