package com.yiyixing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiyixing.dto.request.AnswerRequest;
import com.yiyixing.dto.request.QuestionRequest;
import com.yiyixing.dto.response.QuestionResponse;
import com.yiyixing.entity.User;
import com.yiyixing.security.JwtAuthenticationFilter;
import com.yiyixing.service.AnswerService;
import com.yiyixing.service.QuestionService;
import com.yiyixing.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = QuestionController.class)
@Import(JwtAuthenticationFilter.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private AnswerService answerService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("GET /api/v1/questions — 问题列表")
    class ListQuestions {

        @Test
        @DisplayName("无参数 — 返回 200 分页数据")
        void withoutParams_returnsOkAndPage() throws Exception {
            QuestionResponse q = questionResponse(1L, "Title", "user");
            when(questionService.list(any())).thenReturn(new PageImpl<>(List.of(q)));

            mockMvc.perform(get("/api/v1/questions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.content[0].title").value("Title"));

            verify(questionService).list(any());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/questions/{id} — 问题详情")
    class GetQuestionDetail {

        @Test
        @DisplayName("有效 ID — 返回 200 + 问题详情")
        void validId_returnsOkAndDetail() throws Exception {
            QuestionResponse q = questionResponse(1L, "Detail", "author");
            when(questionService.getById(1L)).thenReturn(q);

            mockMvc.perform(get("/api/v1/questions/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("Detail"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/questions — 创建问题")
    class CreateQuestion {

        @Test
        @DisplayName("有效请求 + JWT userId — 返回 200 + 新建问题")
        void validRequest_returnsCreatedQuestion() throws Exception {
            QuestionRequest req = new QuestionRequest();
            req.setTitle("New Question");
            req.setContent("## Details");
            req.setTags(List.of("java"));

            QuestionResponse resp = questionResponse(1L, "New Question", "author");
            when(questionService.create(eq(1L), any(QuestionRequest.class))).thenReturn(resp);

            mockMvc.perform(post("/api/v1/questions")
                            .requestAttr("userId", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("New Question"));

            verify(questionService).create(eq(1L), any(QuestionRequest.class));
        }

        @Test
        @DisplayName("无标题 — 返回 400 校验错误")
        void missingTitle_returnsBadRequest() throws Exception {
            QuestionRequest req = new QuestionRequest();
            req.setContent("content only");

            mockMvc.perform(post("/api/v1/questions")
                            .requestAttr("userId", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("无内容 — 返回 400 校验错误")
        void missingContent_returnsBadRequest() throws Exception {
            QuestionRequest req = new QuestionRequest();
            req.setTitle("Title only");

            mockMvc.perform(post("/api/v1/questions")
                            .requestAttr("userId", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/questions/{id} — 删除问题")
    class DeleteQuestion {

        @Test
        @DisplayName("作者删除 — 返回 200")
        void authorDeletes_returnsOk() throws Exception {
            doNothing().when(questionService).delete(1L, 1L);

            mockMvc.perform(delete("/api/v1/questions/1").requestAttr("userId", 1L))
                    .andExpect(status().isOk());

            verify(questionService).delete(1L, 1L);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/questions/{id}/answers — 发布回答")
    class CreateAnswer {

        @Test
        @DisplayName("有效请求 — 返回 200 + 新建回答")
        void validRequest_returnsOk() throws Exception {
            AnswerRequest req = new AnswerRequest();
            req.setContent("answer text");

            when(answerService.create(eq(1L), eq(1L), any(AnswerRequest.class)))
                    .thenReturn(answerResponse(1L, "answer text", "author", false));

            mockMvc.perform(post("/api/v1/questions/1/answers")
                            .requestAttr("userId", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value("answer text"));
        }

        @Test
        @DisplayName("空内容 — 返回 400")
        void emptyContent_returnsBadRequest() throws Exception {
            AnswerRequest req = new AnswerRequest();
            req.setContent("");

            mockMvc.perform(post("/api/v1/questions/1/answers")
                            .requestAttr("userId", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/questions/{id}/accept/{aid} — 采纳回答")
    class AcceptAnswer {

        @Test
        @DisplayName("有效请求 — 返回 200 + 已采纳回答")
        void validRequest_returnsAcceptedAnswer() throws Exception {
            when(answerService.accept(1L, 2L, 1L))
                    .thenReturn(answerResponse(2L, "accepted answer", "author", true));

            mockMvc.perform(put("/api/v1/questions/1/accept/2").requestAttr("userId", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accepted").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/questions/{id}/answers — 获取回答列表")
    class GetAnswers {

        @Test
        @DisplayName("返回回答列表")
        void success_returnsAnswerList() throws Exception {
            when(answerService.getByQuestionId(1L))
                    .thenReturn(List.of(answerResponse(1L, "A1", "u1", false)));

            mockMvc.perform(get("/api/v1/questions/1/answers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].content").value("A1"));
        }
    }

    // -- fixtures --

    private QuestionResponse questionResponse(Long id, String title, String username) {
        QuestionResponse resp = new QuestionResponse();
        resp.setId(id);
        resp.setTitle(title);
        resp.setViewCount(0);
        resp.setAnswerCount(0);
        resp.setStatus("OPEN");
        User u = new User();
        u.setId(1L);
        u.setUsername(username);
        var brief = new com.yiyixing.dto.response.UserBrief();
        brief.setId(1L);
        brief.setUsername(username);
        resp.setAuthor(brief);
        resp.setCreatedAt(Instant.now());
        resp.setUpdatedAt(Instant.now());
        return resp;
    }

    private com.yiyixing.dto.response.AnswerResponse answerResponse(
            Long id, String content, String username, boolean accepted) {
        var resp = new com.yiyixing.dto.response.AnswerResponse();
        resp.setId(id);
        resp.setContent(content);
        resp.setAccepted(accepted);
        var brief = new com.yiyixing.dto.response.UserBrief();
        brief.setId(1L);
        brief.setUsername(username);
        resp.setAuthor(brief);
        resp.setCreatedAt(Instant.now());
        resp.setUpdatedAt(Instant.now());
        return resp;
    }
}
