package com.fakeco.instafake.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private String comment;
    private String author;
    private Timestamp timestamp;

    public CommentResponse(Long id, String comment, String author, Timestamp timestamp) {
        this.id = id;
        this.comment = comment;
        this.author = author;
        this.timestamp = timestamp;
    }
}
