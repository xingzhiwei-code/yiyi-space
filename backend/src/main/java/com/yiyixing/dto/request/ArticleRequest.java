package com.yiyixing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过 200 字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private List<String> tags;
}
