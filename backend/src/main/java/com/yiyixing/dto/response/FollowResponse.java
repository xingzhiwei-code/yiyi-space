package com.yiyixing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {

    private Long id;
    private UserBrief follower;
    private UserBrief following;
    private Instant createdAt;

    public static FollowResponse from(com.yiyixing.entity.Follow f) {
        FollowResponse resp = new FollowResponse();
        resp.setId(f.getId());
        resp.setFollower(UserBrief.from(f.getFollower()));
        resp.setFollowing(UserBrief.from(f.getFollowing()));
        resp.setCreatedAt(f.getCreatedAt());
        return resp;
    }
}
