package com.yiyixing.service;

import com.yiyixing.dto.response.InteractionResponse;
import com.yiyixing.entity.UserInteraction.TargetType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InteractionService {

    /**
     * 点赞/取消点赞（toggle）。
     * 返回当前操作后的状态和计数。
     */
    InteractionResponse toggleLike(Long userId, TargetType targetType, Long targetId);

    /**
     * 收藏/取消收藏（toggle）。
     */
    InteractionResponse toggleFavorite(Long userId, TargetType targetType, Long targetId);

    /**
     * 检查用户是否已点赞。
     */
    boolean isLiked(Long userId, TargetType targetType, Long targetId);

    /**
     * 检查用户是否已收藏。
     */
    boolean isFavorited(Long userId, TargetType targetType, Long targetId);

    /**
     * 获取我的收藏列表。
     */
    Page<InteractionResponse> getMyFavorites(Long userId, Pageable pageable);
}
