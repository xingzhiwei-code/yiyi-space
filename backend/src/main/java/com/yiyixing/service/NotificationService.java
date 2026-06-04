package com.yiyixing.service;

import com.yiyixing.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Page<NotificationResponse> list(Long userId, Pageable pageable);

    Page<NotificationResponse> listUnread(Long userId, Pageable pageable);

    long unreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);
}
