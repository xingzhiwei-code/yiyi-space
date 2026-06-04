package com.yiyixing.service;

import com.yiyixing.dto.request.ArticleRequest;
import com.yiyixing.dto.response.ArticleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {

    ArticleResponse create(Long authorId, ArticleRequest request);

    ArticleResponse getById(Long id);

    ArticleResponse getBySlug(String slug);

    Page<ArticleResponse> list(Pageable pageable);

    ArticleResponse update(Long id, Long userId, ArticleRequest request);

    void delete(Long id, Long userId);

    ArticleResponse publish(Long id, Long userId);
}
