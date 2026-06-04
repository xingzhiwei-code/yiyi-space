package com.yiyixing.service;

import com.yiyixing.dto.response.NotificationResponse;
import com.yiyixing.entity.Notification;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.NotificationRepository;
import com.yiyixing.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Nested
    @DisplayName("通知列表")
    class ListNotifications {

        @Test
        @DisplayName("返回所有通知")
        void success_returnsAllNotifications() {
            Page<Notification> page = new PageImpl<>(List.of(notification()));
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L, PageRequest.of(0, 20)))
                    .thenReturn(page);

            var result = notificationService.list(1L, PageRequest.of(0, 20));

            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("返回未读通知")
        void success_returnsUnreadNotifications() {
            Page<Notification> page = new PageImpl<>(List.of(notification()));
            when(notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(1L, false, PageRequest.of(0, 20)))
                    .thenReturn(page);

            var result = notificationService.listUnread(1L, PageRequest.of(0, 20));

            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("返回未读计数")
        void unreadCount_returnsNumber() {
            when(notificationRepository.countByUserIdAndReadFalse(1L)).thenReturn(5L);

            assertEquals(5L, notificationService.unreadCount(1L));
        }
    }

    @Nested
    @DisplayName("标记已读")
    class MarkAsRead {

        @Test
        @DisplayName("标记单个通知已读")
        void markSingle_success() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification()));
            when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            notificationService.markAsRead(1L, 1L);

            verify(notificationRepository).save(any());
        }

        @Test
        @DisplayName("标记非自己的通知 — 抛 403")
        void markOthersNotification_throwsForbidden() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification()));

            ApiException ex = assertThrows(ApiException.class,
                    () -> notificationService.markAsRead(1L, 999L));
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        }

        @Test
        @DisplayName("通知不存在 — 抛 404")
        void notFound_throwsNotFound() {
            when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApiException.class, () -> notificationService.markAsRead(99L, 1L));
        }
    }

    @Nested
    @DisplayName("全部标记已读")
    class MarkAllAsRead {

        @Test
        @DisplayName("将所有未读通知标记为已读")
        void success_marksAllRead() {
            Page<Notification> page = new PageImpl<>(List.of(notification()));
            when(notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(eq(1L), eq(false), any(PageRequest.class)))
                    .thenReturn(page);

            notificationService.markAllAsRead(1L);

            verify(notificationRepository).saveAll(any());
        }
    }

    // -- fixtures --

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("testuser");
        return u;
    }

    private Notification notification() {
        Notification n = new Notification();
        n.setId(1L);
        n.setUser(user());
        n.setType(Notification.Type.ANSWER);
        n.setRead(false);
        n.setContent("有人回答了你的问题");
        n.setCreatedAt(Instant.now());
        return n;
    }
}
