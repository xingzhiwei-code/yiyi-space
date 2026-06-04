package com.yiyixing.service;

import com.yiyixing.dto.response.FollowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    void follow(Long followerId, Long followingId);

    void unfollow(Long followerId, Long followingId);

    boolean isFollowing(Long followerId, Long followingId);

    Page<FollowResponse> getFollowers(Long userId, Pageable pageable);

    Page<FollowResponse> getFollowing(Long userId, Pageable pageable);

    long countFollowers(Long userId);

    long countFollowing(Long userId);
}
