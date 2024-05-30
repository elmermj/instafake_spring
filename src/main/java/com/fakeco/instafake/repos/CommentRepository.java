package com.fakeco.instafake.repos;

import com.fakeco.instafake.models.CommentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentModel, Long> {

    @Query(value = "SELECT * FROM comment_model WHERE post_id = :post ORDER BY created_at DESC", nativeQuery = true)
    List<CommentModel> getCommentsByPostId(@Param("post") Long post);

}
