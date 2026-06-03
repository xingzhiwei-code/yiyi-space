package com.yiyixing.controller;

import com.yiyixing.dto.request.AnswerRequest;
import com.yiyixing.dto.request.QuestionRequest;
import com.yiyixing.dto.response.AnswerResponse;
import com.yiyixing.dto.response.ApiResponse;
import com.yiyixing.dto.response.QuestionResponse;
import com.yiyixing.service.AnswerService;
import com.yiyixing.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    public QuestionController(QuestionService questionService, AnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    /** 问题列表 — 公开 */
    @GetMapping
    public ApiResponse<Page<QuestionResponse>> list(Pageable pageable) {
        return ApiResponse.success(questionService.list(pageable));
    }

    /** 问题详情 — 公开 */
    @GetMapping("/{id}")
    public ApiResponse<QuestionResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(questionService.getById(id));
    }

    /** 创建问题 — 需认证 */
    @PostMapping
    public ApiResponse<QuestionResponse> create(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.success(questionService.create(userId, request));
    }

    /** 删除问题 — 需认证（仅作者） */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        questionService.delete(id, userId);
        return ApiResponse.success(null);
    }

    /** 发布回答 — 需认证 */
    @PostMapping("/{id}/answers")
    public ApiResponse<AnswerResponse> createAnswer(
            @PathVariable("id") Long questionId,
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody AnswerRequest request) {
        return ApiResponse.success(answerService.create(questionId, userId, request));
    }

    /** 获取问题的所有回答 — 公开 */
    @GetMapping("/{id}/answers")
    public ApiResponse<List<AnswerResponse>> getAnswers(@PathVariable Long id) {
        return ApiResponse.success(answerService.getByQuestionId(id));
    }

    /** 采纳回答 — 需认证（仅问题作者） */
    @PutMapping("/{id}/accept/{aid}")
    public ApiResponse<AnswerResponse> accept(
            @PathVariable("id") Long questionId,
            @PathVariable("aid") Long answerId,
            @RequestAttribute("userId") Long userId) {
        return ApiResponse.success(answerService.accept(questionId, answerId, userId));
    }

    /** 删除回答 — 需认证（仅作者） */
    @DeleteMapping("/{id}/answers/{aid}")
    public ApiResponse<Void> deleteAnswer(
            @PathVariable("aid") Long answerId,
            @RequestAttribute("userId") Long userId) {
        answerService.delete(answerId, userId);
        return ApiResponse.success(null);
    }
}
