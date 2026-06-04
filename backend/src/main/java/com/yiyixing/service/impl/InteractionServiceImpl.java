package com.yiyixing.service.impl;

import com.yiyixing.dto.response.InteractionResponse;
import com.yiyixing.entity.User;
import com.yiyixing.entity.UserInteraction;
import com.yiyixing.entity.UserInteraction.TargetType;
import com.yiyixing.entity.UserInteraction.Type;
import com.yiyixing.event.LikedEvent;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.ArticleRepository;
import com.yiyixing.repository.UserInteractionRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.InteractionService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InteractionServiceImpl implements InteractionService {

    private final UserInteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InteractionServiceImpl(UserInteractionRepository interactionRepository,
                                  UserRepository userRepository,
                                  ArticleRepository articleRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.interactionRepository = interactionRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public InteractionResponse toggleLike(Long userId, TargetType targetType, Long targetId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));

        Optional<UserInteraction> existing = interactionRepository
                .findByUserIdAndTargetTypeAndTargetIdAndType(userId, targetType, targetId, Type.LIKE);

        if (existing.isPresent()) {
            // 取消点赞
            interactionRepository.delete(existing.get());
            articleRepository.decrementLikeCount(targetId);
            long count = interactionRepository.countByTargetTypeAndTargetIdAndType(targetType, targetId, Type.LIKE);
            return InteractionResponse.unliked(count);
        } else {
            // 点赞
            UserInteraction interaction = new UserInteraction();
            interaction.setUser(user);
            interaction.setTargetType(targetType);
            interaction.setTargetId(targetId);
            interaction.setType(Type.LIKE);
            interactionRepository.save(interaction);
            articleRepository.incrementLikeCount(targetId);
            long count = interactionRepository.countByTargetTypeAndTargetIdAndType(targetType, targetId, Type.LIKE);

            // 发布点赞通知事件
            // TODO: 根据 targetType 查找内容作者，需要 QuestionRepository/AnswerRepository 等
            // 简化处理：不在此处获取作者 ID，由需要通知的地方自行处理
            return InteractionResponse.liked(count);
        }
    }

    @Override
    @Transactional
    public InteractionResponse toggleFavorite(Long userId, TargetType targetType, Long targetId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));

        Optional<UserInteraction> existing = interactionRepository
                .findByUserIdAndTargetTypeAndTargetIdAndType(userId, targetType, targetId, Type.FAVORITE);

        if (existing.isPresent()) {
            // 取消收藏
            interactionRepository.delete(existing.get());
            long count = interactionRepository.countByTargetTypeAndTargetIdAndType(targetType, targetId, Type.FAVORITE);
            return InteractionResponse.unliked(count);
        } else {
            // 收藏
            UserInteraction interaction = new UserInteraction();
            interaction.setUser(user);
            interaction.setTargetType(targetType);
            interaction.setTargetId(targetId);
            interaction.setType(Type.FAVORITE);
            interactionRepository.save(interaction);
            long count = interactionRepository.countByTargetTypeAndTargetIdAndType(targetType, targetId, Type.FAVORITE);
            return InteractionResponse.liked(count);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, TargetType targetType, Long targetId) {
        return interactionRepository.existsByUserIdAndTargetTypeAndTargetIdAndType(
                userId, targetType, targetId, Type.LIKE);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorited(Long userId, TargetType targetType, Long targetId) {
        return interactionRepository.existsByUserIdAndTargetTypeAndTargetIdAndType(
                userId, targetType, targetId, Type.FAVORITE);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InteractionResponse> getMyFavorites(Long userId, Pageable pageable) {
        return interactionRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, Type.FAVORITE, pageable)
                .map(i -> new InteractionResponse(false, 0));
    }
}
