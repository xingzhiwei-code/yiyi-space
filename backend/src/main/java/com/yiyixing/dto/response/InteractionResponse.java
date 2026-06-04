package com.yiyixing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 互动操作（点赞/收藏）的响应 — 返回当前状态和计数。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InteractionResponse {

    private boolean liked;       // 当前是否已点赞
    private long count;          // 当前总点赞数

    public static InteractionResponse liked(long count) {
        return new InteractionResponse(true, count);
    }

    public static InteractionResponse unliked(long count) {
        return new InteractionResponse(false, count);
    }
}
