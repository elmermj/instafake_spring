package com.fakeco.instafake.dto;

public class PostRequest {

    private String caption;
    private String username;
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public PostRequest(String caption, String username) {
        this.caption = caption;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
