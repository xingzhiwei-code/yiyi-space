package com.yiyixing.service;

import com.yiyixing.dto.request.QuestionRequest;
import com.yiyixing.dto.response.QuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

    QuestionResponse create(Long authorId, QuestionRequest request);

    QuestionResponse getById(Long id);

    Page<QuestionResponse> list(Pageable pageable);

    Page<QuestionResponse> listByTag(String tagName, Pageable pageable);

    void delete(Long id, Long userId);
}
