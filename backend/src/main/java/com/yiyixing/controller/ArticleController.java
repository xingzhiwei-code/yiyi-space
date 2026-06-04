package com.yiyixing.controller;

import com.yiyixing.dto.request.ArticleRequest;
import com.yiyixing.dto.response.ApiResponse;
import com.yiyixing.dto.response.ArticleResponse;
import com.yiyixing.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /** 文章列表 — 公开（仅已发布） */
    @GetMapping
    public ApiResponse<Page<ArticleResponse>> list(Pageable pageable) {
        return ApiResponse.success(articleService.list(pageable));
    }

    /** 按 slug 获取文章 — 公开（仅已发布） */
    @GetMapping("/slug/{slug}")
    public ApiResponse<ArticleResponse> getBySlug(@PathVariable String slug) {
        return ApiResponse.success(articleService.getBySlug(slug));
    }

    /** 按 ID 获取文章 — 需认证（草稿/已发布均可） */
    @GetMapping("/{id}")
    public ApiResponse<ArticleResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(articleService.getById(id));
    }

    /** 创建文章（草稿） — 需认证 */
    @PostMapping
    public ApiResponse<ArticleResponse> create(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody ArticleRequest request) {
        return ApiResponse.success(articleService.create(userId, request));
    }

    /** 更新文章 — 需认证（仅作者） */
    @PutMapping("/{id}")
    public ApiResponse<ArticleResponse> update(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody ArticleRequest request) {
        return ApiResponse.success(articleService.update(id, userId, request));
    }

    /** 删除文章 — 需认证（仅作者） */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        articleService.delete(id, userId);
        return ApiResponse.success(null);
    }

    /** 发布草稿 — 需认证（仅作者） */
    @PutMapping("/{id}/publish")
    public ApiResponse<ArticleResponse> publish(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        return ApiResponse.success(articleService.publish(id, userId));
    }
}
