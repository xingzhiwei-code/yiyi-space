package com.yiyixing.service.impl;

import com.yiyixing.dto.response.FollowResponse;
import com.yiyixing.entity.Follow;
import com.yiyixing.entity.User;
import com.yiyixing.event.FollowedEvent;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.FollowRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.FollowService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FollowServiceImpl(FollowRepository followRepository,
                             UserRepository userRepository,
                             ApplicationEventPublisher eventPublisher) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "不能关注自己");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new ApiException(HttpStatus.CONFLICT, "已关注该用户");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);

        // 发布关注事件 → 通知被关注者
        eventPublisher.publishEvent(new FollowedEvent(this, followerId, followingId));
    }

    @Override
    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "未关注该用户"));
        followRepository.delete(follow);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponse> getFollowers(Long userId, Pageable pageable) {
        return followRepository.findByFollowingId(userId, pageable)
                .map(FollowResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponse> getFollowing(Long userId, Pageable pageable) {
        return followRepository.findByFollowerId(userId, pageable)
                .map(FollowResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowers(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowing(Long userId) {
        return followRepository.countByFollowerId(userId);
    }
}
