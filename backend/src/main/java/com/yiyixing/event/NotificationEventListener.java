package com.yiyixing.event;

import com.yiyixing.entity.Notification;
import com.yiyixing.entity.Notification.TargetType;
import com.yiyixing.entity.Notification.Type;
import com.yiyixing.entity.User;
import com.yiyixing.repository.NotificationRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知事件监听器 — 将业务事件转换为通知记录。
 * 采用同步方式（同一事务），保证数据一致性。
 */
@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);
    private static final int MAX_FOLLOWERS_NOTIFICATION = 100;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationEventListener(NotificationRepository notificationRepository,
                                     UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAnswerCreated(AnswerCreatedEvent event) {
        // 不通知自己
        if (event.getQuestionAuthorId().equals(event.getAnswerAuthorId())) {
            return;
        }

        Notification notification = buildNotification(
                event.getQuestionAuthorId(), event.getAnswerAuthorId(),
                Type.ANSWER, com.yiyixing.entity.Notification.TargetType.QUESTION,
                event.getQuestionId(),
                "有人回答了你的问题");
        notificationRepository.save(notification);
        log.info("Answer notification: user {} answered question {} for user {}",
                event.getAnswerAuthorId(), event.getQuestionId(), event.getQuestionAuthorId());
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAnswerAccepted(AnswerAcceptedEvent event) {
        Notification notification = buildNotification(
                event.getAnswerAuthorId(), null,
                Type.ACCEPT, com.yiyixing.entity.Notification.TargetType.ANSWER,
                event.getAnswerId(),
                "你的回答被采纳");
        notificationRepository.save(notification);
        log.info("Accept notification: answer {} accepted for user {}",
                event.getAnswerId(), event.getAnswerAuthorId());
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCommentCreated(CommentCreatedEvent event) {
        // 不通知自己
        if (event.getCommentAuthorId().equals(event.getTargetUserId())) {
            return;
        }

        TargetType targetType = mapToNotificationTargetType(event.getTargetType());
        Notification notification = buildNotification(
                event.getTargetUserId(), event.getCommentAuthorId(),
                Type.COMMENT, targetType,
                event.getTargetId(),
                "有人评论了你的内容");
        notificationRepository.save(notification);
        log.info("Comment notification: user {} commented on {} {} for user {}",
                event.getCommentAuthorId(), event.getTargetType(), event.getTargetId(),
                event.getTargetUserId());
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onLiked(LikedEvent event) {
        // 不通知自己
        if (event.getLikerId().equals(event.getTargetUserId())) {
            return;
        }

        TargetType targetType = mapToNotificationTargetType(
                com.yiyixing.entity.Comment.TargetType.valueOf(event.getTargetType()));
        Notification notification = buildNotification(
                event.getTargetUserId(), event.getLikerId(),
                Type.LIKE, targetType,
                event.getTargetId(),
                "有人点赞了你的内容");
        notificationRepository.save(notification);
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onFollowed(FollowedEvent event) {
        Notification notification = buildNotification(
                event.getFollowingId(), event.getFollowerId(),
                Type.FOLLOW, null, null,
                "有人关注了你");
        notificationRepository.save(notification);
        log.info("Follow notification: user {} followed user {}",
                event.getFollowerId(), event.getFollowingId());
    }

    // -- helpers --

    private Notification buildNotification(Long userId, Long fromUserId,
                                           Type type, TargetType targetType,
                                           Long targetId, String content) {
        Notification n = new Notification();
        n.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在")));
        if (fromUserId != null) {
            n.setFromUser(userRepository.findById(fromUserId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在")));
        }
        n.setType(type);
        n.setTargetType(targetType);
        n.setTargetId(targetId);
        n.setContent(content);
        n.setRead(false);
        return n;
    }

    private TargetType mapToNotificationTargetType(com.yiyixing.entity.Comment.TargetType t) {
        return switch (t) {
            case QUESTION -> TargetType.QUESTION;
            case ANSWER -> TargetType.ANSWER;
            case ARTICLE -> TargetType.ARTICLE;
        };
    }
}
