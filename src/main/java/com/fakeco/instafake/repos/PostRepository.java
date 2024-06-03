package com.fakeco.instafake.repos;

import com.fakeco.instafake.models.PostModel;
import com.fakeco.instafake.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<PostModel, Long> {
    @Query(value = "SELECT * FROM post_model ORDER BY created_at DESC", nativeQuery = true)
    List<PostModel> getPostsFromNewest();

    @Query(value = "SELECT * FROM post_model WHERE user_id = :user ORDER BY created_at DESC", nativeQuery = true)
    List<PostModel> getUserPosts(@Param("user") Long user);

    @Query(value =
            "SELECT post_model.* FROM post_model " +
                    "LEFT JOIN follow_model ON post_model.user_id = follow_model.other_user_id " +
                    "WHERE follow_model.user_id = :user OR post_model.user_id = :user " +
                    "GROUP BY post_model.id " +
                    "ORDER BY post_model.created_at DESC",
            countQuery = "SELECT COUNT(DISTINCT post_model.id) FROM post_model " +
                    "LEFT JOIN follow_model ON post_model.user_id = follow_model.other_user_id " +
                    "WHERE follow_model.user_id = :user OR post_model.user_id = :user",
            nativeQuery = true)
    Page<PostModel> getPostsFromUserTimeline(@Param("user") Long user, Pageable pageable);

    @Query(value =
            "SELECT post_model.* FROM post_model " +
                    "ORDER BY post_model.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM post_model",
            nativeQuery = true)
    Page<PostModel> getExplorePosts(Pageable pageable);



}
