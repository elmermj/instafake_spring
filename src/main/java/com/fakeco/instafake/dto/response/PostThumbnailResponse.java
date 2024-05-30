package com.fakeco.instafake.dto.response;

import com.fakeco.instafake.models.PostModel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class PostThumbnailResponse {
    private Long postId;
    private String fileUrl;
    private String fileName;
    private String caption;
    private Timestamp createdAt;
    private Long userId;

    public PostThumbnailResponse(PostModel post) {
        this.postId = post.getId();
        this.fileUrl = post.getFileUrl();
        this.caption = post.getCaption();
        this.createdAt = post.getCreatedAt();
        this.userId = post.getUser().getId();
        this.fileName = post.getFileName();
    }
}
