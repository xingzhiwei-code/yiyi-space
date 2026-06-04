package com.yiyixing.service;

import com.yiyixing.dto.response.FollowResponse;
import com.yiyixing.entity.Follow;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.FollowRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.impl.FollowServiceImpl;
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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FollowServiceImpl followService;

    @Nested
    @DisplayName("关注")
    class DoFollow {

        @Test
        @DisplayName("成功关注 — 创建关注关系 + 发布事件")
        void success_createsFollowAndPublishesEvent() {
            when(followRepository.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
            when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L)));
            when(followRepository.save(any(Follow.class))).thenAnswer(inv -> inv.getArgument(0));

            followService.follow(1L, 2L);

            verify(followRepository).save(any());
            verify(eventPublisher).publishEvent(any());
        }

        @Test
        @DisplayName("关注自己 — 抛 400")
        void followSelf_throwsBadRequest() {
            ApiException ex = assertThrows(ApiException.class,
                    () -> followService.follow(1L, 1L));
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }

        @Test
        @DisplayName("重复关注 — 抛 409")
        void alreadyFollowing_throwsConflict() {
            when(followRepository.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(true);

            ApiException ex = assertThrows(ApiException.class,
                    () -> followService.follow(1L, 2L));
            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("取消关注")
    class Unfollow {

        @Test
        @DisplayName("成功取消关注")
        void success_deletesFollow() {
            Follow follow = new Follow();
            follow.setId(1L);
            when(followRepository.findByFollowerIdAndFollowingId(1L, 2L))
                    .thenReturn(Optional.of(follow));

            assertDoesNotThrow(() -> followService.unfollow(1L, 2L));
            verify(followRepository).delete(follow);
        }

        @Test
        @DisplayName("未关注 — 抛 404")
        void notFollowing_throwsNotFound() {
            when(followRepository.findByFollowerIdAndFollowingId(1L, 2L))
                    .thenReturn(Optional.empty());

            ApiException ex = assertThrows(ApiException.class,
                    () -> followService.unfollow(1L, 2L));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("关注状态")
    class CheckFollow {

        @Test
        @DisplayName("已关注 → 返回 true")
        void isFollowing_returnsTrue() {
            when(followRepository.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(true);
            assertTrue(followService.isFollowing(1L, 2L));
        }

        @Test
        @DisplayName("未关注 → 返回 false")
        void isNotFollowing_returnsFalse() {
            when(followRepository.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(false);
            assertFalse(followService.isFollowing(1L, 2L));
        }
    }

    @Nested
    @DisplayName("粉丝/关注列表")
    class Lists {

        @Test
        @DisplayName("返回粉丝列表")
        void getFollowers_returnsPage() {
            Page<Follow> page = new PageImpl<>(List.of(follow()));
            when(followRepository.findByFollowingId(1L, PageRequest.of(0, 20))).thenReturn(page);

            var result = followService.getFollowers(1L, PageRequest.of(0, 20));

            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("返回关注列表")
        void getFollowing_returnsPage() {
            Page<Follow> page = new PageImpl<>(List.of(follow()));
            when(followRepository.findByFollowerId(1L, PageRequest.of(0, 20))).thenReturn(page);

            var result = followService.getFollowing(1L, PageRequest.of(0, 20));

            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("返回粉丝数")
        void countFollowers_returnsCount() {
            when(followRepository.countByFollowingId(1L)).thenReturn(42L);
            assertEquals(42L, followService.countFollowers(1L));
        }

        @Test
        @DisplayName("返回关注数")
        void countFollowing_returnsCount() {
            when(followRepository.countByFollowerId(1L)).thenReturn(10L);
            assertEquals(10L, followService.countFollowing(1L));
        }
    }

    // -- fixtures --

    private User user(Long id) {
        User u = new User();
        u.setId(id);
        u.setUsername("user" + id);
        return u;
    }

    private Follow follow() {
        Follow f = new Follow();
        f.setId(1L);
        f.setFollower(user(1L));
        f.setFollowing(user(2L));
        return f;
    }
}
