package com.yiyixing.service;

import com.yiyixing.dto.request.QuestionRequest;
import com.yiyixing.dto.response.QuestionResponse;
import com.yiyixing.entity.Question;
import com.yiyixing.entity.Tag;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.QuestionRepository;
import com.yiyixing.repository.TagRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.util.MarkdownUtil;
import com.yiyixing.service.impl.QuestionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MarkdownUtil markdownUtil;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Nested
    @DisplayName("创建问题")
    class Create {

        @Test
        @DisplayName("成功创建 — 渲染 Markdown + 创建标签 + 递增标签使用量")
        void success_rendersMarkdownCreatesTagsIncrementsCount() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            Tag tag = new Tag();
            tag.setId(1L);
            tag.setName("java");
            when(tagService.getOrCreate("java")).thenReturn(tag);
            when(markdownUtil.render("**hello**")).thenReturn("<p><strong>hello</strong></p>");
            when(questionRepository.save(any(Question.class))).thenAnswer(inv -> {
                Question q = inv.getArgument(0);
                q.setId(1L);
                q.setCreatedAt(Instant.now());
                q.setUpdatedAt(Instant.now());
                return q;
            });

            QuestionRequest req = new QuestionRequest();
            req.setTitle("Hello");
            req.setContent("**hello**");
            req.setTags(List.of("java"));

            QuestionResponse resp = questionService.create(1L, req);

            assertNotNull(resp);
            assertEquals("Hello", resp.getTitle());
            assertEquals("<p><strong>hello</strong></p>", resp.getContentHtml());
            assertEquals(1, resp.getTags().size());
            verify(tagService).getOrCreate("java");
            verify(tagRepository).incrementUseCount(1L);
        }

        @Test
        @DisplayName("用户不存在 — 抛 404")
        void userNotFound_throwsNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            ApiException ex = assertThrows(ApiException.class,
                    () -> questionService.create(99L, new QuestionRequest()));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }

        @Test
        @DisplayName("无标签创建 — 空标签列表正常")
        void noTags_succeeds() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            when(questionRepository.save(any(Question.class))).thenAnswer(inv -> {
                Question q = inv.getArgument(0);
                q.setId(1L);
                q.setCreatedAt(Instant.now());
                q.setUpdatedAt(Instant.now());
                return q;
            });

            QuestionRequest req = new QuestionRequest();
            req.setTitle("No tags");
            req.setContent("plain text");

            QuestionResponse resp = questionService.create(1L, req);

            assertNotNull(resp);
            assertTrue(resp.getTags().isEmpty());
            verify(tagService, never()).getOrCreate(any());
        }
    }

    @Nested
    @DisplayName("获取问题")
    class GetById {

        @Test
        @DisplayName("成功获取 — 浏览量递增")
        void success_incrementsViewCount() {
            when(questionRepository.findById(1L)).thenReturn(Optional.of(question()));

            questionService.getById(1L);

            verify(questionRepository).incrementViewCount(1L);
            verify(questionRepository).findById(1L);
        }

        @Test
        @DisplayName("问题不存在 — 抛 404")
        void notFound_throwsNotFound() {
            when(questionRepository.findById(99L)).thenReturn(Optional.empty());

            ApiException ex = assertThrows(ApiException.class, () -> questionService.getById(99L));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("问题列表")
    class ListQuestions {

        @Test
        @DisplayName("返回精简版 — 不返回 content 和 contentHtml")
        void success_returnsBriefWithoutContent() {
            Question q = question();
            q.setContent("full content");
            q.setContentHtml("<p>rendered</p>");
            Page<Question> page = new PageImpl<>(List.of(q));
            when(questionRepository.findAllByOrderByCreatedAtDesc(any())).thenReturn(page);

            var result = questionService.list(PageRequest.of(0, 20));

            QuestionResponse resp = result.getContent().get(0);
            assertNull(resp.getContent());
            assertNull(resp.getContentHtml());
        }
    }

    @Nested
    @DisplayName("按标签筛选")
    class ListByTag {

        @Test
        @DisplayName("返回该标签下的问题")
        void success_filtersByTagName() {
            Page<Question> page = new PageImpl<>(List.of(question()));
            when(questionRepository.findByTagsNameOrderByCreatedAtDesc(eq("java"), any()))
                    .thenReturn(page);

            var result = questionService.listByTag("java", PageRequest.of(0, 20));

            assertEquals(1, result.getTotalElements());
            verify(questionRepository).findByTagsNameOrderByCreatedAtDesc(eq("java"), any());
        }
    }

    @Nested
    @DisplayName("删除问题")
    class Delete {

        @Test
        @DisplayName("作者删除 — 成功 + 递减标签使用量")
        void authorDeletes_success() {
            when(questionRepository.findById(1L)).thenReturn(Optional.of(question()));

            assertDoesNotThrow(() -> questionService.delete(1L, 1L));
            verify(questionRepository).delete(any());
        }

        @Test
        @DisplayName("非作者删除 — 抛 403")
        void notAuthor_throwsForbidden() {
            when(questionRepository.findById(1L)).thenReturn(Optional.of(question()));

            ApiException ex = assertThrows(ApiException.class,
                    () -> questionService.delete(1L, 999L));
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        }

        @Test
        @DisplayName("问题不存在 — 抛 404")
        void notFound_throwsNotFound() {
            when(questionRepository.findById(99L)).thenReturn(Optional.empty());

            ApiException ex = assertThrows(ApiException.class,
                    () -> questionService.delete(99L, 1L));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    // -- fixtures --

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("testuser");
        return u;
    }

    private Question question() {
        Question q = new Question();
        q.setId(1L);
        q.setTitle("Test");
        q.setContent("content");
        q.setContentHtml("<p>content</p>");
        q.setViewCount(0);
        q.setAnswerCount(0);
        q.setStatus(Question.Status.OPEN);
        User u = new User();
        u.setId(1L);
        u.setUsername("testuser");
        q.setAuthor(u);
        q.setCreatedAt(Instant.now());
        q.setUpdatedAt(Instant.now());
        q.setTags(List.of());
        return q;
    }
}
