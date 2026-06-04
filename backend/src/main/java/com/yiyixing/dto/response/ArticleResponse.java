package com.yiyixing.dto.response;

import com.yiyixing.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String contentHtml;
    private String slug;
    private String status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private UserBrief author;
    private List<TagResponse> tags;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;

    public static ArticleResponse from(Article a) {
        ArticleResponse resp = new ArticleResponse();
        resp.setId(a.getId());
        resp.setTitle(a.getTitle());
        resp.setContent(a.getContent());
        resp.setContentHtml(a.getContentHtml());
        resp.setSlug(a.getSlug());
        resp.setStatus(a.getStatus().name());
        resp.setViewCount(a.getViewCount());
        resp.setLikeCount(a.getLikeCount());
        resp.setFavoriteCount(a.getFavoriteCount());
        resp.setCommentCount(a.getCommentCount());
        resp.setCreatedAt(a.getCreatedAt());
        resp.setUpdatedAt(a.getUpdatedAt());
        resp.setPublishedAt(a.getPublishedAt());
        if (a.getAuthor() != null) {
            resp.setAuthor(UserBrief.from(a.getAuthor()));
        }
        if (a.getTags() != null) {
            resp.setTags(a.getTags().stream().map(TagResponse::from).toList());
        }
        return resp;
    }

    /** 列表页精简版 — 不返回完整 content */
    public static ArticleResponse brief(Article a) {
        ArticleResponse resp = from(a);
        resp.setContent(null);
        resp.setContentHtml(null);
        return resp;
    }
}
