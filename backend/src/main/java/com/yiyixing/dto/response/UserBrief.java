package com.yiyixing.dto.response;

import com.yiyixing.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户精简信息 — 嵌套在 Question/Answer/Comment 响应中。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBrief {

    private Long id;
    private String username;
    private String avatarUrl;

    public static UserBrief from(User u) {
        return new UserBrief(u.getId(), u.getUsername(), u.getAvatarUrl());
    }
}
