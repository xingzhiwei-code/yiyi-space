package com.yiyixing.controller;

import com.yiyixing.dto.response.ApiResponse;
import com.yiyixing.dto.response.NotificationResponse;
import com.yiyixing.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** 通知列表 — 需认证 */
    @GetMapping
    public ApiResponse<Page<NotificationResponse>> list(
            @RequestAttribute("userId") Long userId,
            Pageable pageable) {
        return ApiResponse.success(notificationService.list(userId, pageable));
    }

    /** 未读通知列表 — 需认证 */
    @GetMapping("/unread")
    public ApiResponse<Page<NotificationResponse>> listUnread(
            @RequestAttribute("userId") Long userId,
            Pageable pageable) {
        return ApiResponse.success(notificationService.listUnread(userId, pageable));
    }

    /** 未读计数 — 需认证 */
    @GetMapping("/unread/count")
    public ApiResponse<Long> unreadCount(@RequestAttribute("userId") Long userId) {
        return ApiResponse.success(notificationService.unreadCount(userId));
    }

    /** 标记单个已读 — 需认证 */
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        notificationService.markAsRead(id, userId);
        return ApiResponse.success(null);
    }

    /** 全部标记已读 — 需认证 */
    @PutMapping("/read")
    public ApiResponse<Void> markAllAsRead(@RequestAttribute("userId") Long userId) {
        notificationService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }
}
