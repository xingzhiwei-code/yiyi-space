package com.yiyixing.service.impl;

import com.yiyixing.dto.request.QuestionRequest;
import com.yiyixing.dto.response.QuestionResponse;
import com.yiyixing.entity.Question;
import com.yiyixing.entity.Tag;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.QuestionRepository;
import com.yiyixing.repository.TagRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.service.QuestionService;
import com.yiyixing.service.TagService;
import com.yiyixing.util.MarkdownUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final MarkdownUtil markdownUtil;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               UserRepository userRepository,
                               TagService tagService,
                               TagRepository tagRepository,
                               MarkdownUtil markdownUtil) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.markdownUtil = markdownUtil;
    }

    @Override
    @Transactional
    public QuestionResponse create(Long authorId, QuestionRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));

        Question question = new Question();
        question.setAuthor(author);
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setContentHtml(markdownUtil.render(request.getContent()));

        // 处理标签
        List<Tag> tags = new ArrayList<>();
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                Tag tag = tagService.getOrCreate(tagName);
                tags.add(tag);
                tagRepository.incrementUseCount(tag.getId());
            }
        }
        question.setTags(tags);

        questionRepository.save(question);
        return QuestionResponse.from(question);
    }

    @Override
    @Transactional
    public QuestionResponse getById(Long id) {
        questionRepository.incrementViewCount(id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "问题不存在"));
        return QuestionResponse.from(question);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> list(Pageable pageable) {
        return questionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(QuestionResponse::brief);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> listByTag(String tagName, Pageable pageable) {
        return questionRepository.findByTagsNameOrderByCreatedAtDesc(tagName, pageable)
                .map(QuestionResponse::brief);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "问题不存在"));
        if (!question.getAuthor().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "只有作者可以删除自己的问题");
        }
        // 递减标签使用量
        question.getTags().forEach(tag -> tagRepository.decrementUseCount(tag.getId()));
        questionRepository.delete(question);
    }
}
