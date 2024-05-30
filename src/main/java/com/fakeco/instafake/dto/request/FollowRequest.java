package com.fakeco.instafake.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FollowRequest {
    private Long id;
    private Long otherId;
}
