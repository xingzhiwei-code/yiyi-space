package com.yiyixing.service.impl;

import com.yiyixing.dto.response.NotificationResponse;
import com.yiyixing.entity.Notification;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.NotificationRepository;
import com.yiyixing.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> list(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> listUnread(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false, pageable)
                .map(NotificationResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "通知不存在"));
        if (!notification.getUser().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "无权操作此通知");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        Page<Notification> unread = notificationRepository
                .findByUserIdAndReadOrderByCreatedAtDesc(userId, false, Pageable.ofSize(100));
        unread.getContent().forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread.getContent());
    }
}
