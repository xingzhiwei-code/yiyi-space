package com.yiyixing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String type;
    private UserBrief fromUser;
    private String targetType;
    private Long targetId;
    private String content;
    private Boolean read;
    private Instant createdAt;

    public static NotificationResponse from(com.yiyixing.entity.Notification n) {
        NotificationResponse resp = new NotificationResponse();
        resp.setId(n.getId());
        resp.setType(n.getType().name());
        if (n.getFromUser() != null) {
            resp.setFromUser(UserBrief.from(n.getFromUser()));
        }
        resp.setTargetType(n.getTargetType() != null ? n.getTargetType().name() : null);
        resp.setTargetId(n.getTargetId());
        resp.setContent(n.getContent());
        resp.setRead(n.getRead());
        resp.setCreatedAt(n.getCreatedAt());
        return resp;
    }
}
