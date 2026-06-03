package com.yiyixing.service;

import com.yiyixing.dto.request.AnswerRequest;
import com.yiyixing.dto.response.AnswerResponse;

import java.util.List;

public interface AnswerService {

    AnswerResponse create(Long questionId, Long authorId, AnswerRequest request);

    List<AnswerResponse> getByQuestionId(Long questionId);

    AnswerResponse accept(Long questionId, Long answerId, Long userId);

    void delete(Long answerId, Long userId);
}
