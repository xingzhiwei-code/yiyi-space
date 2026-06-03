package com.yiyixing.dto.response;

import com.yiyixing.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private UserBrief author;
    private Instant createdAt;
    private Instant updatedAt;

    public static CommentResponse from(Comment c) {
        CommentResponse resp = new CommentResponse();
        resp.setId(c.getId());
        resp.setContent(c.getContent());
        resp.setCreatedAt(c.getCreatedAt());
        resp.setUpdatedAt(c.getUpdatedAt());
        if (c.getAuthor() != null) {
            resp.setAuthor(UserBrief.from(c.getAuthor()));
        }
        return resp;
    }
}
