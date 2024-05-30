package com.fakeco.instafake.dto;

import com.fakeco.instafake.models.PostModel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Base64;
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

    public PostResponse(PostModel post, List<CommentResponse> comments) {
        this.id = post.getId();
        this.fileUrl = post.getFileUrl();
        this.caption = post.getCaption();
        this.createdAt = post.getCreatedAt();
        this.userId = post.getUser().getId();
        this.fileName = post.getFileName();
        this.comments = comments;
//        this.fileContent = Base64.getEncoder().encodeToString(fileBytes);
    }
}