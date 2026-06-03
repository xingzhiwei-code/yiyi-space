package com.yiyixing.service;

import com.yiyixing.dto.response.TagResponse;
import com.yiyixing.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {

    Page<TagResponse> list(Pageable pageable);

    Tag getOrCreate(String name);
}
