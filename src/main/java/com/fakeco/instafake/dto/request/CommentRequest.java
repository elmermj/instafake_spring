package com.fakeco.instafake.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String comment;
    private String username;

    public CommentRequest(String comment, String username) {
        this.comment = comment;
        this.username = username;
    }
}
