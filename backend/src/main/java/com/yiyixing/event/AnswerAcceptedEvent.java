package com.yiyixing.event;

import org.springframework.context.ApplicationEvent;

/**
 * 回答被采纳事件 — 触发回答作者的通知。
 */
public class AnswerAcceptedEvent extends ApplicationEvent {

    private final Long answerId;
    private final Long answerAuthorId;
    private final Long questionId;

    public AnswerAcceptedEvent(Object source, Long answerId,
                               Long answerAuthorId, Long questionId) {
        super(source);
        this.answerId = answerId;
        this.answerAuthorId = answerAuthorId;
        this.questionId = questionId;
    }

    public Long getAnswerId() { return answerId; }
    public Long getAnswerAuthorId() { return answerAuthorId; }
    public Long getQuestionId() { return questionId; }
}
