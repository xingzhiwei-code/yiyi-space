package com.yiyixing.service.impl;

import com.yiyixing.dto.response.TagResponse;
import com.yiyixing.entity.Tag;
import com.yiyixing.repository.TagRepository;
import com.yiyixing.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagResponse> list(Pageable pageable) {
        return tagRepository.findByOrderByUseCountDesc(pageable)
                .map(TagResponse::from);
    }

    @Override
    @Transactional
    public Tag getOrCreate(String name) {
        String normalizedName = name.trim().toLowerCase();
        return tagRepository.findByName(normalizedName)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(normalizedName);
                    return tagRepository.save(tag);
                });
    }
}
