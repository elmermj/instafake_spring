package com.fakeco.instafake.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LikeRequest {
    private String userId;
    private String postId;
    public LikeRequest( String userId, String postId) {
        this.userId = userId;
        this.postId = postId;
    }
}
