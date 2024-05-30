package com.fakeco.instafake.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProfileResponse {
    private Long id;
    private String username;
    private String profImageUrl;
    private String name;
    private String bio;
    private int followers;
    private int followings;
    private List<PostThumbnailResponse> thumbnails;

    public ProfileResponse(UserDTOResponse userDTO, List<PostThumbnailResponse> thumbnails, int followers, int followings){
        this.id = userDTO.getId();
        this.username = userDTO.getUsername();
        this.profImageUrl = userDTO.getProfImageUrl();
        this.name = userDTO.getName();
        this.bio = userDTO.getBio();
        this.followers = followers;
        this.followings = followings;
        this.thumbnails = thumbnails;
    }
}
