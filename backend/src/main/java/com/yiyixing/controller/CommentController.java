package com.yiyixing.controller;

import com.yiyixing.dto.request.CommentRequest;
import com.yiyixing.dto.response.ApiResponse;
import com.yiyixing.dto.response.CommentResponse;
import com.yiyixing.entity.Comment.TargetType;
import com.yiyixing.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 获取评论 — 公开 */
    @GetMapping
    public ApiResponse<Page<CommentResponse>> getByTarget(
            @RequestParam TargetType targetType,
            @RequestParam Long targetId,
            Pageable pageable) {
        return ApiResponse.success(commentService.getByTarget(targetType, targetId, pageable));
    }

    /** 创建评论 — 需认证 */
    @PostMapping
    public ApiResponse<CommentResponse> create(
            @RequestParam TargetType targetType,
            @RequestParam Long targetId,
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody CommentRequest request) {
        return ApiResponse.success(commentService.create(targetType, targetId, userId, request));
    }

    /** 删除评论 — 需认证（仅作者） */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        commentService.delete(id, userId);
        return ApiResponse.success(null);
    }
}
