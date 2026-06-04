package com.yiyixing.service.impl;

import com.yiyixing.dto.request.CommentRequest;
import com.yiyixing.dto.response.CommentResponse;
import com.yiyixing.entity.Comment;
import com.yiyixing.entity.Comment.TargetType;
import com.yiyixing.entity.User;
import com.yiyixing.event.CommentCreatedEvent;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.AnswerRepository;
import com.yiyixing.repository.CommentRepository;
import com.yiyixing.repository.QuestionRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.CommentService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              QuestionRepository questionRepository,
                              AnswerRepository answerRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.eventPublisher = eventPublisher;
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

        // 查找目标作者并发布评论事件 → 通知
        Long targetUserId = findTargetAuthor(targetType, targetId);
        if (targetUserId != null) {
            eventPublisher.publishEvent(new CommentCreatedEvent(this,
                    targetUserId, authorId, targetType, targetId, request.getContent()));
        }

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

    /**
     * 根据目标类型和 ID 查找内容作者。
     * ARTICLE 暂不支持（Phase 3 后期补充）。
     */
    private Long findTargetAuthor(TargetType targetType, Long targetId) {
        return switch (targetType) {
            case QUESTION -> questionRepository.findById(targetId)
                    .map(q -> q.getAuthor().getId()).orElse(null);
            case ANSWER -> answerRepository.findById(targetId)
                    .map(a -> a.getAuthor().getId()).orElse(null);
            case ARTICLE -> null; // TODO: 注入 ArticleRepository 后补充
        };
    }
}
