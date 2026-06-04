package com.yiyixing.event;

import org.springframework.context.ApplicationEvent;

/**
 * 关注事件 — 触发被关注者的通知。
 */
public class FollowedEvent extends ApplicationEvent {

    private final Long followerId;
    private final Long followingId;

    public FollowedEvent(Object source, Long followerId, Long followingId) {
        super(source);
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Long getFollowerId() { return followerId; }
    public Long getFollowingId() { return followingId; }
}
