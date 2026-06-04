package com.yiyixing.controller;

import com.yiyixing.dto.response.ApiResponse;
import com.yiyixing.dto.response.FollowResponse;
import com.yiyixing.service.FollowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    /** 关注用户 — 需认证 */
    @PostMapping("/{followingId}")
    public ApiResponse<Void> follow(
            @RequestAttribute("userId") Long followerId,
            @PathVariable Long followingId) {
        followService.follow(followerId, followingId);
        return ApiResponse.success(null);
    }

    /** 取消关注 — 需认证 */
    @DeleteMapping("/{followingId}")
    public ApiResponse<Void> unfollow(
            @RequestAttribute("userId") Long followerId,
            @PathVariable Long followingId) {
        followService.unfollow(followerId, followingId);
        return ApiResponse.success(null);
    }

    /** 检查是否已关注 — 需认证 */
    @GetMapping("/check/{followingId}")
    public ApiResponse<Boolean> checkFollow(
            @RequestAttribute("userId") Long followerId,
            @PathVariable Long followingId) {
        return ApiResponse.success(followService.isFollowing(followerId, followingId));
    }

    /** 粉丝列表 — 公开 */
    @GetMapping("/{userId}/followers")
    public ApiResponse<Page<FollowResponse>> getFollowers(
            @PathVariable Long userId,
            Pageable pageable) {
        return ApiResponse.success(followService.getFollowers(userId, pageable));
    }

    /** 关注列表 — 公开 */
    @GetMapping("/{userId}/following")
    public ApiResponse<Page<FollowResponse>> getFollowing(
            @PathVariable Long userId,
            Pageable pageable) {
        return ApiResponse.success(followService.getFollowing(userId, pageable));
    }

    /** 粉丝数 — 公开 */
    @GetMapping("/{userId}/followers/count")
    public ApiResponse<Long> countFollowers(@PathVariable Long userId) {
        return ApiResponse.success(followService.countFollowers(userId));
    }

    /** 关注数 — 公开 */
    @GetMapping("/{userId}/following/count")
    public ApiResponse<Long> countFollowing(@PathVariable Long userId) {
        return ApiResponse.success(followService.countFollowing(userId));
    }
}
