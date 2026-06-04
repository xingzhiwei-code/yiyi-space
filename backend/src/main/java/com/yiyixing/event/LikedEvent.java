package com.yiyixing.event;

import org.springframework.context.ApplicationEvent;

/**
 * 点赞事件 — 触发被点赞者的通知。
 */
public class LikedEvent extends ApplicationEvent {

    private final Long targetUserId;     // 被点赞内容的作者
    private final Long likerId;
    private final String targetType;     // "QUESTION", "ANSWER", "ARTICLE", "COMMENT"
    private final Long targetId;

    public LikedEvent(Object source, Long targetUserId, Long likerId,
                      String targetType, Long targetId) {
        super(source);
        this.targetUserId = targetUserId;
        this.likerId = likerId;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public Long getTargetUserId() { return targetUserId; }
    public Long getLikerId() { return likerId; }
    public String getTargetType() { return targetType; }
    public Long getTargetId() { return targetId; }
}
