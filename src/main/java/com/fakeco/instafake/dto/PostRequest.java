package com.fakeco.instafake.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostRequest {

    private String caption;
    private String username;

    public PostRequest(String caption, String username) {
        this.caption = caption;
        this.username = username;
    }

}
