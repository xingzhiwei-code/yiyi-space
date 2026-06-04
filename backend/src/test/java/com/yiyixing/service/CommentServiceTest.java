package com.yiyixing.service;

import com.yiyixing.dto.request.CommentRequest;
import com.yiyixing.dto.response.CommentResponse;
import com.yiyixing.entity.Comment;
import com.yiyixing.entity.Comment.TargetType;
import com.yiyixing.entity.Question;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.AnswerRepository;
import com.yiyixing.repository.CommentRepository;
import com.yiyixing.repository.QuestionRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.impl.CommentServiceImpl;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Nested
    @DisplayName("创建评论")
    class Create {

        @Test
        @DisplayName("成功创建 — 绑定用户")
        void success_bindsAuthor() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            Question q = new Question();
            q.setId(1L);
            q.setAuthor(user());
            when(questionRepository.findById(1L)).thenReturn(Optional.of(q));
            when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
                Comment c = inv.getArgument(0);
                c.setId(1L);
                return c;
            });

            CommentRequest req = new CommentRequest();
            req.setContent("好问题！");
            CommentResponse resp = commentService.create(
                    TargetType.QUESTION, 1L, 1L, req);

            assertNotNull(resp);
            assertEquals("好问题！", resp.getContent());
            assertEquals("testuser", resp.getAuthor().getUsername());
            verify(commentRepository).save(any());
        }

        @Test
        @DisplayName("用户不存在 — 抛 404")
        void userNotFound_throwsNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            CommentRequest req = new CommentRequest();
            req.setContent("x");
            ApiException ex = assertThrows(ApiException.class,
                    () -> commentService.create(TargetType.QUESTION, 1L, 99L, req));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("获取评论")
    class GetByTarget {

        @Test
        @DisplayName("按创建时间升序分页返回")
        void success_returnsAscending() {
            Page<Comment> page = new PageImpl<>(java.util.List.of(comment()));
            when(commentRepository.findByTargetTypeAndTargetIdOrderByCreatedAtAsc(
                    eq(TargetType.QUESTION), eq(1L), any())).thenReturn(page);

            var result = commentService.getByTarget(TargetType.QUESTION, 1L, PageRequest.of(0, 20));

            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("删除评论")
    class Delete {

        @Test
        @DisplayName("作者删除 — 成功")
        void authorDeletes_success() {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment()));

            assertDoesNotThrow(() -> commentService.delete(1L, 1L));
            verify(commentRepository).delete(any());
        }

        @Test
        @DisplayName("非作者删除 — 抛 403")
        void notAuthor_throwsForbidden() {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment()));

            ApiException ex = assertThrows(ApiException.class,
                    () -> commentService.delete(1L, 999L));
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        }
    }

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("testuser");
        return u;
    }

    private Comment comment() {
        Comment c = new Comment();
        c.setId(1L);
        c.setTargetType(TargetType.QUESTION);
        c.setTargetId(1L);
        c.setContent("好问题！");
        c.setAuthor(user());
        return c;
    }
}
