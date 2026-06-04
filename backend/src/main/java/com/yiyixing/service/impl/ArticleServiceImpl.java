package com.yiyixing.service.impl;

import com.yiyixing.dto.request.ArticleRequest;
import com.yiyixing.dto.response.ArticleResponse;
import com.yiyixing.entity.Article;
import com.yiyixing.entity.Tag;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.ArticleRepository;
import com.yiyixing.repository.TagRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.ArticleService;
import com.yiyixing.service.TagService;
import com.yiyixing.util.MarkdownUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final String SLUG_PREFIX = "art-";
    private static final int SLUG_RANDOM_LENGTH = 8;
    private static final String SLUG_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final MarkdownUtil markdownUtil;

    public ArticleServiceImpl(ArticleRepository articleRepository,
                              UserRepository userRepository,
                              TagService tagService,
                              TagRepository tagRepository,
                              MarkdownUtil markdownUtil) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.markdownUtil = markdownUtil;
    }

    @Override
    @Transactional
    public ArticleResponse create(Long authorId, ArticleRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));

        Article article = new Article();
        article.setAuthor(author);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setContentHtml(markdownUtil.render(request.getContent()));
        article.setSlug(generateUniqueSlug());

        // 处理标签
        List<Tag> tags = new ArrayList<>();
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                Tag tag = tagService.getOrCreate(tagName);
                tags.add(tag);
                tagRepository.incrementUseCount(tag.getId());
            }
        }
        article.setTags(tags);

        articleRepository.save(article);
        return ArticleResponse.from(article);
    }

    @Override
    @Transactional
    public ArticleResponse getById(Long id) {
        articleRepository.incrementViewCount(id);

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "文章不存在"));
        return ArticleResponse.from(article);
    }

    @Override
    @Transactional
    public ArticleResponse getBySlug(String slug) {
        Article article = articleRepository.findBySlugAndStatus(slug, Article.Status.PUBLISHED)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "文章不存在"));
        articleRepository.incrementViewCount(article.getId());
        return ArticleResponse.from(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleResponse> list(Pageable pageable) {
        return articleRepository.findByStatusOrderByCreatedAtDesc(Article.Status.PUBLISHED, pageable)
                .map(ArticleResponse::brief);
    }

    @Override
    @Transactional
    public ArticleResponse update(Long id, Long userId, ArticleRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "文章不存在"));
        if (!article.getAuthor().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "只有作者可以编辑自己的文章");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setContentHtml(markdownUtil.render(request.getContent()));

        // 更新标签：先递减旧标签，再设置新标签
        article.getTags().forEach(tag -> tagRepository.decrementUseCount(tag.getId()));
        article.getTags().clear();

        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                Tag tag = tagService.getOrCreate(tagName);
                article.getTags().add(tag);
                tagRepository.incrementUseCount(tag.getId());
            }
        }

        articleRepository.save(article);
        return ArticleResponse.from(article);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "文章不存在"));
        if (!article.getAuthor().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "只有作者可以删除自己的文章");
        }
        // 递减标签使用量
        article.getTags().forEach(tag -> tagRepository.decrementUseCount(tag.getId()));
        articleRepository.delete(article);
    }

    @Override
    @Transactional
    public ArticleResponse publish(Long id, Long userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "文章不存在"));
        if (!article.getAuthor().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "只有作者可以发布自己的文章");
        }
        if (article.getStatus() == Article.Status.PUBLISHED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "文章已发布");
        }

        article.setStatus(Article.Status.PUBLISHED);
        article.setPublishedAt(Instant.now());
        articleRepository.save(article);
        return ArticleResponse.from(article);
    }

    // -- helpers --

    /**
     * 生成唯一 slug：art-{nanoid8}
     * 重试最多 5 次（碰撞概率极低）。
     */
    private String generateUniqueSlug() {
        for (int i = 0; i < 5; i++) {
            String slug = SLUG_PREFIX + generateRandomString(SLUG_RANDOM_LENGTH);
            if (articleRepository.findBySlugAndStatus(slug, Article.Status.PUBLISHED).isEmpty()
                    && articleRepository.findBySlugAndStatus(slug, Article.Status.DRAFT).isEmpty()) {
                return slug;
            }
        }
        // 极不可能到达
        return SLUG_PREFIX + generateRandomString(SLUG_RANDOM_LENGTH) + System.currentTimeMillis();
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(SLUG_CHARS.charAt(RANDOM.nextInt(SLUG_CHARS.length())));
        }
        return sb.toString();
    }
}
