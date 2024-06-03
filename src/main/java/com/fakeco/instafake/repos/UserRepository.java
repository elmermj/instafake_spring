package com.fakeco.instafake.repos;

import com.fakeco.instafake.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;


public interface UserRepository extends JpaRepository<UserModel, Long> {

    @Query(value = "SELECT * FROM user_model WHERE username = :username", nativeQuery = true)
    UserModel findByUsername(@Param("username") String username);
    @Query(value = "SELECT * FROM user_model WHERE email = ?1", nativeQuery = true)
    UserModel findByEmail(@Param("email") String email);
    @Query(value = "SELECT * FROM user_model WHERE username LIKE :query",nativeQuery = true)
    List<UserModel> findByQuery(@Param("query") String query);

    @Query(value = "SELECT user.* FROM user_model user, post_model post WHERE user.id = post.user_id AND post.id = :postId", nativeQuery = true)
    UserModel findUserByPostId(@Param("postId") Long postId);

//    @Query(value = "INSERT INTO users (username, realname, email, password, createdAt) VALUES (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
//    void saveUser(String username, String realname, String email, String password, Timestamp createdAt);
}