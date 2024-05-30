package com.fakeco.instafake.services;

import com.fakeco.instafake.models.CommentModel;
import com.fakeco.instafake.models.PostModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.repos.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public void addComment(String comment, UserModel user, PostModel post) {
        CommentModel commentModel = new CommentModel();
        commentModel.setUser(user);
        commentModel.setPost(post);
        commentModel.setBody(comment);
        commentModel.setCreatedAt(Timestamp.from(Instant.now()));

        System.out.println(commentModel);
        commentRepository.save(commentModel);
    }
}
