package com.fakeco.instafake.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class StoryResponse {
    private Long storyId;
    private String storyUrl;
    private String storyAuthor;
    private Long authorId;
    private Timestamp createdAt;

    public StoryResponse(
            Long storyId,
            String storyUrl,
            String storyAuthor,
            Long authorId,
            Timestamp createdAt
    ) {
        this.storyId = storyId;
        this.storyUrl = storyUrl;
        this.storyAuthor = storyAuthor;
        this.authorId = authorId;
        this.createdAt = createdAt;
    }
}
