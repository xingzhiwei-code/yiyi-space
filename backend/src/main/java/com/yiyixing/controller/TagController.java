package com.yiyixing.controller;

import com.yiyixing.dto.response.ApiResponse;
import com.yiyixing.dto.response.QuestionResponse;
import com.yiyixing.dto.response.TagResponse;
import com.yiyixing.service.QuestionService;
import com.yiyixing.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;
    private final QuestionService questionService;

    public TagController(TagService tagService, QuestionService questionService) {
        this.tagService = tagService;
        this.questionService = questionService;
    }

    /** 标签列表 — 公开 */
    @GetMapping
    public ApiResponse<Page<TagResponse>> list(Pageable pageable) {
        return ApiResponse.success(tagService.list(pageable));
    }

    /** 标签下的问题 — 公开 */
    @GetMapping("/{id}/questions")
    public ApiResponse<Page<QuestionResponse>> getQuestions(
            @PathVariable Long id, Pageable pageable) {
        // TODO: need a way to resolve tag name from id; for now use name directly
        return ApiResponse.success(questionService.list(pageable));
    }
}
