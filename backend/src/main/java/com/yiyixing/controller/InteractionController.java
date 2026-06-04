package com.yiyixing.controller;

import com.yiyixing.dto.response.ApiResponse;
import com.yiyixing.dto.response.InteractionResponse;
import com.yiyixing.entity.UserInteraction.TargetType;
import com.yiyixing.service.InteractionService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    /** 点赞/取消点赞 — 需认证 */
    @PostMapping("/like")
    public ApiResponse<InteractionResponse> toggleLike(
            @RequestAttribute("userId") Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        TargetType type = TargetType.valueOf(targetType.toUpperCase());
        return ApiResponse.success(interactionService.toggleLike(userId, type, targetId));
    }

    /** 收藏/取消收藏 — 需认证 */
    @PostMapping("/favorite")
    public ApiResponse<InteractionResponse> toggleFavorite(
            @RequestAttribute("userId") Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        TargetType type = TargetType.valueOf(targetType.toUpperCase());
        return ApiResponse.success(interactionService.toggleFavorite(userId, type, targetId));
    }

    /** 检查是否已点赞 — 需认证 */
    @GetMapping("/like/check")
    public ApiResponse<Boolean> checkLike(
            @RequestAttribute("userId") Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        TargetType type = TargetType.valueOf(targetType.toUpperCase());
        return ApiResponse.success(interactionService.isLiked(userId, type, targetId));
    }

    /** 检查是否已收藏 — 需认证 */
    @GetMapping("/favorite/check")
    public ApiResponse<Boolean> checkFavorite(
            @RequestAttribute("userId") Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        TargetType type = TargetType.valueOf(targetType.toUpperCase());
        return ApiResponse.success(interactionService.isFavorited(userId, type, targetId));
    }

    /** 我的收藏列表 — 需认证 */
    @GetMapping("/me/favorites")
    public ApiResponse<?> getMyFavorites(
            @RequestAttribute("userId") Long userId,
            Pageable pageable) {
        return ApiResponse.success(interactionService.getMyFavorites(userId, pageable));
    }
}
