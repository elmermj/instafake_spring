package com.fakeco.instafake.dto.response;

import com.fakeco.instafake.models.UserModel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTOResponse {
    private Long id;
    private String username;
    private String profImageUrl;
    private String name;
    private String bio;

    public UserDTOResponse(UserModel user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getRealname();
        this.profImageUrl = user.getProfImageUrl();
        this.bio = user.getBio();
    }
}
