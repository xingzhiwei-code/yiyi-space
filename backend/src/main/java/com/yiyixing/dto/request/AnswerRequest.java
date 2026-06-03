package com.yiyixing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {

    @NotBlank(message = "回答内容不能为空")
    private String content;
}
