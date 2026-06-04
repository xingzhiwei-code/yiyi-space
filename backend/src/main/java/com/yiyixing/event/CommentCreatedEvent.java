package com.yiyixing.event;

import com.yiyixing.entity.Comment.TargetType;
import org.springframework.context.ApplicationEvent;

/**
 * 评论创建事件 — 触发通知。
 */
public class CommentCreatedEvent extends ApplicationEvent {

    private final Long targetUserId;    // 被评论内容的作者
    private final Long commentAuthorId;
    private final TargetType targetType;
    private final Long targetId;
    private final String content;

    public CommentCreatedEvent(Object source, Long targetUserId, Long commentAuthorId,
                               TargetType targetType, Long targetId, String content) {
        super(source);
        this.targetUserId = targetUserId;
        this.commentAuthorId = commentAuthorId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.content = content;
    }

    public Long getTargetUserId() { return targetUserId; }
    public Long getCommentAuthorId() { return commentAuthorId; }
    public TargetType getTargetType() { return targetType; }
    public Long getTargetId() { return targetId; }
    public String getContent() { return content; }
}
