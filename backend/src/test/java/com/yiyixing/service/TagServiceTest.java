package com.yiyixing.service;

import com.yiyixing.entity.Tag;
import com.yiyixing.repository.TagRepository;
import com.yiyixing.service.impl.TagServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    @DisplayName("获取已存在的标签 — 不创建新记录")
    void getOrCreate_existingTag_returnsExisting() {
        Tag existing = new Tag();
        existing.setId(1L);
        existing.setName("java");
        existing.setUseCount(5);
        existing.setCreatedAt(Instant.now());
        when(tagRepository.findByName("java")).thenReturn(Optional.of(existing));

        Tag result = tagService.getOrCreate("  JAVA  "); // 带空格大写

        assertNotNull(result);
        assertEquals("java", result.getName());
        verify(tagRepository).findByName("java");
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("获取不存在的标签 — 自动创建")
    void getOrCreate_newTag_createsNew() {
        when(tagRepository.findByName("spring-boot")).thenReturn(Optional.empty());
        Tag saved = new Tag();
        saved.setId(2L);
        saved.setName("spring-boot");
        when(tagRepository.save(any(Tag.class))).thenReturn(saved);

        Tag result = tagService.getOrCreate("spring-boot");

        assertNotNull(result);
        assertEquals("spring-boot", result.getName());
        verify(tagRepository).findByName("spring-boot");
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    @DisplayName("标签列表 — 按使用量降序分页")
    void list_returnsSortedByUseCount() {
        Tag t1 = new Tag(); t1.setName("java"); t1.setUseCount(10);
        Tag t2 = new Tag(); t2.setName("python"); t2.setUseCount(5);
        Page<Tag> page = new PageImpl<>(java.util.List.of(t1, t2));
        when(tagRepository.findByOrderByUseCountDesc(any())).thenReturn(page);

        var result = tagService.list(PageRequest.of(0, 20));

        assertEquals(2, result.getContent().size());
        assertEquals("java", result.getContent().get(0).getName());
    }
}
