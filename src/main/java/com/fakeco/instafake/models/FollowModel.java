package com.fakeco.instafake.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_user_id", nullable = false)
    private UserModel otherUser;

}

//USER ---> [FOLLOW] ---> OTHER USER
//COUNT FOLLOWING BASED ON USER "user_id" COLUMN
//COUNT FOLLOWERS BASED ON OTHER USER "other_user_id" COLUMN