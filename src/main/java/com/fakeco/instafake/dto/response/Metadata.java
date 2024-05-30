package com.fakeco.instafake.dto.response;

import com.fakeco.instafake.models.UserModel;

public class Metadata {
    public String token;
    public UserModel metadata;

    public Metadata(String token, UserModel metadata) {
        this.token = token;
        this.metadata = metadata;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserModel getMetadata() {
        return metadata;
    }

    public void setMetadata(UserModel metadata) {
        this.metadata = metadata;
    }
}
