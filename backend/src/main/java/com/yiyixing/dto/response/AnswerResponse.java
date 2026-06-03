package com.yiyixing.dto.response;

import com.yiyixing.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {

    private Long id;
    private String content;
    private String contentHtml;
    private Boolean accepted;
    private UserBrief author;
    private Instant createdAt;
    private Instant updatedAt;

    public static AnswerResponse from(Answer a) {
        AnswerResponse resp = new AnswerResponse();
        resp.setId(a.getId());
        resp.setContent(a.getContent());
        resp.setContentHtml(a.getContentHtml());
        resp.setAccepted(a.getAccepted());
        resp.setCreatedAt(a.getCreatedAt());
        resp.setUpdatedAt(a.getUpdatedAt());
        if (a.getAuthor() != null) {
            resp.setAuthor(UserBrief.from(a.getAuthor()));
        }
        return resp;
    }
}
