package com.fakeco.instafake.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LikeRequest {
    private String id;
    private String userId;
    private String postId;
    public LikeRequest(String id, String userId, String postId) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
    }
}
