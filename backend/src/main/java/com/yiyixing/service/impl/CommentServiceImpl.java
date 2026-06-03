package com.yiyixing.service.impl;

import com.yiyixing.dto.request.CommentRequest;
import com.yiyixing.dto.response.CommentResponse;
import com.yiyixing.entity.Comment;
import com.yiyixing.entity.Comment.TargetType;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.CommentRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentResponse create(TargetType targetType, Long targetId, Long authorId, CommentRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));

        Comment comment = new Comment();
        comment.setTargetType(targetType);
        comment.setTargetId(targetId);
        comment.setAuthor(author);
        comment.setContent(request.getContent());

        commentRepository.save(comment);
        return CommentResponse.from(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getByTarget(TargetType targetType, Long targetId, Pageable pageable) {
        return commentRepository.findByTargetTypeAndTargetIdOrderByCreatedAtAsc(targetType, targetId, pageable)
                .map(CommentResponse::from);
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "评论不存在"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "只有作者可以删除自己的评论");
        }
        commentRepository.delete(comment);
    }
}
