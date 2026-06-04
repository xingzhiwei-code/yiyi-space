package com.yiyixing.service.impl;

import com.yiyixing.event.AnswerAcceptedEvent;
import com.yiyixing.event.AnswerCreatedEvent;
import com.yiyixing.dto.request.AnswerRequest;
import com.yiyixing.dto.response.AnswerResponse;
import com.yiyixing.entity.Answer;
import com.yiyixing.entity.Question;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.AnswerRepository;
import com.yiyixing.repository.QuestionRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.AnswerService;
import com.yiyixing.util.MarkdownUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final MarkdownUtil markdownUtil;
    private final ApplicationEventPublisher eventPublisher;

    public AnswerServiceImpl(AnswerRepository answerRepository,
                             QuestionRepository questionRepository,
                             UserRepository userRepository,
                             MarkdownUtil markdownUtil,
                             ApplicationEventPublisher eventPublisher) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.markdownUtil = markdownUtil;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public AnswerResponse create(Long questionId, Long authorId, AnswerRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "问题不存在"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setAuthor(author);
        answer.setContent(request.getContent());
        answer.setContentHtml(markdownUtil.render(request.getContent()));

        answerRepository.save(answer);
        questionRepository.incrementAnswerCount(questionId);

        // 发布回答创建事件 → 通知问题作者
        eventPublisher.publishEvent(new AnswerCreatedEvent(this,
                questionId, question.getAuthor().getId(), authorId));

        return AnswerResponse.from(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponse> getByQuestionId(Long questionId) {
        return answerRepository.findByQuestionIdOrderByCreatedAtAsc(questionId)
                .stream()
                .map(AnswerResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public AnswerResponse accept(Long questionId, Long answerId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "问题不存在"));
        if (!question.getAuthor().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "只有问题作者可以采纳回答");
        }

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "回答不存在"));
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "该回答不属于此问题");
        }

        answer.setAccepted(true);
        answerRepository.save(answer);

        question.setStatus(Question.Status.RESOLVED);
        questionRepository.save(question);

        // 发布回答被采纳事件 → 通知回答作者
        eventPublisher.publishEvent(new AnswerAcceptedEvent(this,
                answerId, answer.getAuthor().getId(), questionId));

        return AnswerResponse.from(answer);
    }

    @Override
    @Transactional
    public void delete(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "回答不存在"));
        if (!answer.getAuthor().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "只有作者可以删除自己的回答");
        }
        answerRepository.delete(answer);
    }
}
