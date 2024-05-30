package com.fakeco.instafake.repos;

import com.fakeco.instafake.models.LikeModel;
import com.fakeco.instafake.models.PostModel;
import com.fakeco.instafake.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeModel, Long> {

    @Query(value = "SELECT * FROM like_model WHERE post_id = :post", nativeQuery = true)
    List<LikeModel> getLikesByPostId(@Param("post") Long id);

    @Query(value = "SELECT COUNT(*) FROM like_model WHERE post_id = :post", nativeQuery = true)
    int getLikesCountByPostId(@Param("post") Long id);

    @Query(value =
            "SELECT user_model.* " +
            "FROM user_model JOIN like_table " +
            "ON user_model.id = like_table.user_id " +
            "WHERE like_table.post_id = :post",
            nativeQuery = true)
    List<UserModel> getUsersLikesByPost(PostModel post);
}
