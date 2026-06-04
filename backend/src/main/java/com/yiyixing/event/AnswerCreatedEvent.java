package com.yiyixing.event;

import org.springframework.context.ApplicationEvent;

/**
 * 回答创建事件 — 触发问题作者的通知。
 */
public class AnswerCreatedEvent extends ApplicationEvent {

    private final Long questionId;
    private final Long questionAuthorId;
    private final Long answerAuthorId;

    public AnswerCreatedEvent(Object source, Long questionId,
                              Long questionAuthorId, Long answerAuthorId) {
        super(source);
        this.questionId = questionId;
        this.questionAuthorId = questionAuthorId;
        this.answerAuthorId = answerAuthorId;
    }

    public Long getQuestionId() { return questionId; }
    public Long getQuestionAuthorId() { return questionAuthorId; }
    public Long getAnswerAuthorId() { return answerAuthorId; }
}
