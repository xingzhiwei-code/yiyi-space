package com.yiyixing.dto.response;

import com.yiyixing.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {

    private Long id;
    private String name;
    private String description;
    private Integer useCount;
    private Instant createdAt;

    public static TagResponse from(Tag t) {
        return new TagResponse(
                t.getId(), t.getName(), t.getDescription(),
                t.getUseCount(), t.getCreatedAt());
    }
}
