package com.fakeco.instafake.repos;

import com.fakeco.instafake.models.FollowModel;
import com.fakeco.instafake.models.UserModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowModel, Long> {

    @Query(value = "SELECT * FROM follow_model WHERE other_user_id = :user", nativeQuery = true)
    List<FollowModel> getFollowers(@Param("user") Long user);

    @Query(value = "SELECT * FROM follow_model WHERE user_id = :user", nativeQuery = true)
    List<FollowModel> getFollowings(@Param("user") Long user);

    @Query(value =
            "SELECT user_model.id " +
            "FROM user_model JOIN follow_model "+
            "ON user_model.id = follow_model.user_id " +
            "WHERE follow_model.other_user_id = :user",
            nativeQuery = true
    )
    List<Long> getListOfFollowers(@Param("user") Long user);

    @Query(value =
            "SELECT user_model.id " +
            "FROM user_model JOIN follow_model "+
            "ON user_model.id = follow_model.other_user_id " +
            "WHERE follow_model.user_id = :user",
            nativeQuery = true
    )
    List<Long> getListOfFollowing(@Param("user") Long user);

    @Query(value = "DELETE FROM follow_table WHERE user_id = :user", nativeQuery = true)
    void deleteByUserId(@Param("user") Long user);

    @Query(value = "DELETE FROM follow_model WHERE other_user_id = :user", nativeQuery = true)
    void removeFollowerById(@Param("user") Long user); // Corrected column name
}


