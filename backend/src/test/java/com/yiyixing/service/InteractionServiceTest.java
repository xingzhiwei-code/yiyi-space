package com.yiyixing.service;

import com.yiyixing.dto.response.InteractionResponse;
import com.yiyixing.entity.User;
import com.yiyixing.entity.UserInteraction;
import com.yiyixing.entity.UserInteraction.TargetType;
import com.yiyixing.entity.UserInteraction.Type;
import com.yiyixing.repository.ArticleRepository;
import com.yiyixing.repository.UserInteractionRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.impl.InteractionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractionServiceTest {

    @Mock
    private UserInteractionRepository interactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InteractionServiceImpl interactionService;

    @Nested
    @DisplayName("点赞")
    class ToggleLike {

        @Test
        @DisplayName("未点赞 → 点赞成功")
        void like_whenNotLiked_succeeds() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            when(interactionRepository.findByUserIdAndTargetTypeAndTargetIdAndType(
                    1L, TargetType.ARTICLE, 1L, Type.LIKE)).thenReturn(Optional.empty());
            when(interactionRepository.save(any(UserInteraction.class))).thenAnswer(inv -> inv.getArgument(0));
            when(interactionRepository.countByTargetTypeAndTargetIdAndType(
                    TargetType.ARTICLE, 1L, Type.LIKE)).thenReturn(5L);

            InteractionResponse resp = interactionService.toggleLike(1L, TargetType.ARTICLE, 1L);

            assertTrue(resp.isLiked());
            assertEquals(5L, resp.getCount());
            verify(interactionRepository).save(any());
            verify(articleRepository).incrementLikeCount(1L);
        }

        @Test
        @DisplayName("已点赞 → 取消点赞")
        void unlike_whenLiked_succeeds() {
            UserInteraction existing = new UserInteraction();
            existing.setId(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            when(interactionRepository.findByUserIdAndTargetTypeAndTargetIdAndType(
                    1L, TargetType.ARTICLE, 1L, Type.LIKE)).thenReturn(Optional.of(existing));
            when(interactionRepository.countByTargetTypeAndTargetIdAndType(
                    TargetType.ARTICLE, 1L, Type.LIKE)).thenReturn(4L);

            InteractionResponse resp = interactionService.toggleLike(1L, TargetType.ARTICLE, 1L);

            assertFalse(resp.isLiked());
            assertEquals(4L, resp.getCount());
            verify(interactionRepository).delete(existing);
            verify(articleRepository).decrementLikeCount(1L);
        }
    }

    @Nested
    @DisplayName("收藏")
    class ToggleFavorite {

        @Test
        @DisplayName("未收藏 → 收藏成功")
        void favorite_whenNotFavorited_succeeds() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            when(interactionRepository.findByUserIdAndTargetTypeAndTargetIdAndType(
                    1L, TargetType.ARTICLE, 1L, Type.FAVORITE)).thenReturn(Optional.empty());
            when(interactionRepository.save(any(UserInteraction.class))).thenAnswer(inv -> inv.getArgument(0));
            when(interactionRepository.countByTargetTypeAndTargetIdAndType(
                    TargetType.ARTICLE, 1L, Type.FAVORITE)).thenReturn(3L);

            InteractionResponse resp = interactionService.toggleFavorite(1L, TargetType.ARTICLE, 1L);

            assertTrue(resp.isLiked());
            assertEquals(3L, resp.getCount());
            verify(interactionRepository).save(any());
        }

        @Test
        @DisplayName("已收藏 → 取消收藏")
        void unfavorite_whenFavorited_succeeds() {
            UserInteraction existing = new UserInteraction();
            existing.setId(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            when(interactionRepository.findByUserIdAndTargetTypeAndTargetIdAndType(
                    1L, TargetType.ARTICLE, 1L, Type.FAVORITE)).thenReturn(Optional.of(existing));
            when(interactionRepository.countByTargetTypeAndTargetIdAndType(
                    TargetType.ARTICLE, 1L, Type.FAVORITE)).thenReturn(2L);

            InteractionResponse resp = interactionService.toggleFavorite(1L, TargetType.ARTICLE, 1L);

            assertFalse(resp.isLiked());
            assertEquals(2L, resp.getCount());
            verify(interactionRepository).delete(existing);
        }
    }

    @Nested
    @DisplayName("状态检查")
    class CheckStatus {

        @Test
        @DisplayName("已点赞 → 返回 true")
        void isLiked_returnsTrue() {
            when(interactionRepository.existsByUserIdAndTargetTypeAndTargetIdAndType(
                    1L, TargetType.ARTICLE, 1L, Type.LIKE)).thenReturn(true);

            assertTrue(interactionService.isLiked(1L, TargetType.ARTICLE, 1L));
        }

        @Test
        @DisplayName("未点赞 → 返回 false")
        void isLiked_returnsFalse() {
            when(interactionRepository.existsByUserIdAndTargetTypeAndTargetIdAndType(
                    1L, TargetType.ARTICLE, 1L, Type.LIKE)).thenReturn(false);

            assertFalse(interactionService.isLiked(1L, TargetType.ARTICLE, 1L));
        }
    }

    @Nested
    @DisplayName("我的收藏")
    class GetMyFavorites {

        @Test
        @DisplayName("返回收藏列表")
        void success_returnsFavorites() {
            Page<UserInteraction> page = new PageImpl<>(List.of(new UserInteraction()));
            when(interactionRepository.findByUserIdAndTypeOrderByCreatedAtDesc(
                    eq(1L), eq(Type.FAVORITE), any(PageRequest.class))).thenReturn(page);

            var result = interactionService.getMyFavorites(1L, PageRequest.of(0, 20));

            assertEquals(1, result.getTotalElements());
        }
    }

    // -- fixtures --

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("testuser");
        return u;
    }
}
