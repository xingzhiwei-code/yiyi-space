package com.yiyixing.service;

import com.yiyixing.dto.request.CommentRequest;
import com.yiyixing.dto.response.CommentResponse;
import com.yiyixing.entity.Comment.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponse create(TargetType targetType, Long targetId, Long authorId, CommentRequest request);

    Page<CommentResponse> getByTarget(TargetType targetType, Long targetId, Pageable pageable);

    void delete(Long commentId, Long userId);
}
