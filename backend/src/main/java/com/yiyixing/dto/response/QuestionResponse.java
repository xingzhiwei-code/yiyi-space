package com.yiyixing.dto.response;

import com.yiyixing.entity.Question;
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
public class QuestionResponse {

    private Long id;
    private String title;
    private String content;
    private String contentHtml;
    private Integer viewCount;
    private Integer answerCount;
    private String status;
    private UserBrief author;
    private List<TagResponse> tags;
    private Instant createdAt;
    private Instant updatedAt;

    public static QuestionResponse from(Question q) {
        QuestionResponse resp = new QuestionResponse();
        resp.setId(q.getId());
        resp.setTitle(q.getTitle());
        resp.setContent(q.getContent());
        resp.setContentHtml(q.getContentHtml());
        resp.setViewCount(q.getViewCount());
        resp.setAnswerCount(q.getAnswerCount());
        resp.setStatus(q.getStatus().name());
        resp.setCreatedAt(q.getCreatedAt());
        resp.setUpdatedAt(q.getUpdatedAt());
        if (q.getAuthor() != null) {
            resp.setAuthor(UserBrief.from(q.getAuthor()));
        }
        if (q.getTags() != null) {
            resp.setTags(q.getTags().stream().map(TagResponse::from).toList());
        }
        return resp;
    }

    /** 列表页精简版 — 不返回完整 content */
    public static QuestionResponse brief(Question q) {
        QuestionResponse resp = from(q);
        resp.setContent(null);
        resp.setContentHtml(null);
        return resp;
    }
}
