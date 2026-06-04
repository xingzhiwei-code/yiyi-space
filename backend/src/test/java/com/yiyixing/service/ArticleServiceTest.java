package com.yiyixing.service;

import com.yiyixing.dto.request.ArticleRequest;
import com.yiyixing.dto.response.ArticleResponse;
import com.yiyixing.entity.Article;
import com.yiyixing.entity.Tag;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.ArticleRepository;
import com.yiyixing.repository.TagRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.impl.ArticleServiceImpl;
import com.yiyixing.util.MarkdownUtil;
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
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MarkdownUtil markdownUtil;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    @DisplayName("创建文章")
    class Create {

        @Test
        @DisplayName("成功创建 — 自动生成 slug + 渲染 Markdown + 创建标签")
        void success_generatesSlugRendersMarkdownCreatesTags() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            Tag tag = new Tag();
            tag.setId(1L);
            tag.setName("java");
            when(tagService.getOrCreate("java")).thenReturn(tag);
            when(markdownUtil.render("**hello**")).thenReturn("<p><strong>hello</strong></p>");
            when(articleRepository.save(any(Article.class))).thenAnswer(inv -> {
                Article a = inv.getArgument(0);
                a.setId(1L);
                a.setCreatedAt(Instant.now());
                a.setUpdatedAt(Instant.now());
                return a;
            });
            when(articleRepository.findBySlugAndStatus(anyString(), any())).thenReturn(Optional.empty());

            ArticleRequest req = new ArticleRequest();
            req.setTitle("Hello");
            req.setContent("**hello**");
            req.setTags(List.of("java"));

            ArticleResponse resp = articleService.create(1L, req);

            assertNotNull(resp);
            assertEquals("Hello", resp.getTitle());
            assertTrue(resp.getSlug().startsWith("art-"));
            assertEquals("<p><strong>hello</strong></p>", resp.getContentHtml());
            assertEquals("DRAFT", resp.getStatus());
            verify(tagRepository).incrementUseCount(1L);
        }

        @Test
        @DisplayName("用户不存在 — 抛 404")
        void userNotFound_throwsNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            ApiException ex = assertThrows(ApiException.class,
                    () -> articleService.create(99L, new ArticleRequest()));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("获取文章")
    class GetById {

        @Test
        @DisplayName("成功获取 — 浏览量递增")
        void success_incrementsViewCount() {
            when(articleRepository.findById(1L)).thenReturn(Optional.of(article()));

            articleService.getById(1L);

            verify(articleRepository).incrementViewCount(1L);
            verify(articleRepository).findById(1L);
        }

        @Test
        @DisplayName("文章不存在 — 抛 404")
        void notFound_throwsNotFound() {
            when(articleRepository.findById(99L)).thenReturn(Optional.empty());

            ApiException ex = assertThrows(ApiException.class, () -> articleService.getById(99L));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("按 slug 获取文章")
    class GetBySlug {

        @Test
        @DisplayName("成功获取已发布文章")
        void success_returnsPublishedArticle() {
            Article a = article();
            a.setStatus(Article.Status.PUBLISHED);
            when(articleRepository.findBySlugAndStatus("art-abc12345", Article.Status.PUBLISHED))
                    .thenReturn(Optional.of(a));

            ArticleResponse resp = articleService.getBySlug("art-abc12345");

            assertNotNull(resp);
            assertEquals("art-abc12345", resp.getSlug());
        }

        @Test
        @DisplayName("草稿文章 — 抛 404")
        void draftArticle_throwsNotFound() {
            when(articleRepository.findBySlugAndStatus("art-abc12345", Article.Status.PUBLISHED))
                    .thenReturn(Optional.empty());

            assertThrows(ApiException.class, () -> articleService.getBySlug("art-abc12345"));
        }
    }

    @Nested
    @DisplayName("文章列表")
    class ListArticles {

        @Test
        @DisplayName("返回已发布文章的精简版")
        void success_returnsBriefPublishedArticles() {
            Article a = article();
            a.setStatus(Article.Status.PUBLISHED);
            Page<Article> page = new PageImpl<>(List.of(a));
            when(articleRepository.findByStatusOrderByCreatedAtDesc(eq(Article.Status.PUBLISHED), any(PageRequest.class)))
                    .thenReturn(page);

            var result = articleService.list(PageRequest.of(0, 20));

            assertEquals(1, result.getTotalElements());
            assertNull(result.getContent().get(0).getContent());
        }
    }

    @Nested
    @DisplayName("发布草稿")
    class Publish {

        @Test
        @DisplayName("作者发布草稿 — 成功")
        void authorPublishes_success() {
            Article a = article();
            a.setStatus(Article.Status.DRAFT);
            when(articleRepository.findById(1L)).thenReturn(Optional.of(a));
            when(articleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ArticleResponse resp = articleService.publish(1L, 1L);

            assertEquals("PUBLISHED", resp.getStatus());
            assertNotNull(resp.getPublishedAt());
        }

        @Test
        @DisplayName("非作者发布 — 抛 403")
        void notAuthor_throwsForbidden() {
            when(articleRepository.findById(1L)).thenReturn(Optional.of(article()));

            ApiException ex = assertThrows(ApiException.class,
                    () -> articleService.publish(1L, 999L));
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        }

        @Test
        @DisplayName("重复发布 — 抛 400")
        void alreadyPublished_throwsBadRequest() {
            Article a = article();
            a.setStatus(Article.Status.PUBLISHED);
            when(articleRepository.findById(1L)).thenReturn(Optional.of(a));

            ApiException ex = assertThrows(ApiException.class,
                    () -> articleService.publish(1L, 1L));
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("删除文章")
    class Delete {

        @Test
        @DisplayName("作者删除 — 成功 + 递减标签使用量")
        void authorDeletes_success() {
            when(articleRepository.findById(1L)).thenReturn(Optional.of(article()));

            assertDoesNotThrow(() -> articleService.delete(1L, 1L));
            verify(articleRepository).delete(any());
        }

        @Test
        @DisplayName("非作者删除 — 抛 403")
        void notAuthor_throwsForbidden() {
            when(articleRepository.findById(1L)).thenReturn(Optional.of(article()));

            ApiException ex = assertThrows(ApiException.class,
                    () -> articleService.delete(1L, 999L));
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        }
    }

    // -- fixtures --

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("testuser");
        return u;
    }

    private Article article() {
        Article a = new Article();
        a.setId(1L);
        a.setTitle("Test Article");
        a.setContent("content");
        a.setContentHtml("<p>content</p>");
        a.setSlug("art-abc12345");
        a.setStatus(Article.Status.DRAFT);
        a.setViewCount(0);
        a.setLikeCount(0);
        a.setFavoriteCount(0);
        a.setCommentCount(0);
        a.setAuthor(user());
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());
        a.setTags(List.of());
        return a;
    }
}
