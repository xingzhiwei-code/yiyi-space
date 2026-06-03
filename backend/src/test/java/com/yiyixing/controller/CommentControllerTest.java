package com.yiyixing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiyixing.dto.request.CommentRequest;
import com.yiyixing.entity.Comment.TargetType;
import com.yiyixing.security.JwtAuthenticationFilter;
import com.yiyixing.service.CommentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
@Import(JwtAuthenticationFilter.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("GET /api/v1/comments — 获取评论")
    class GetComments {

        @Test
        @DisplayName("有效参数 — 返回分页评论列表")
        void validParams_returnsPageOfComments() throws Exception {
            var resp = commentResponse(1L, "Good!", "author");
            when(commentService.getByTarget(eq(TargetType.QUESTION), eq(1L), any()))
                    .thenReturn(new PageImpl<>(List.of(resp)));

            mockMvc.perform(get("/api/v1/comments")
                            .param("targetType", "QUESTION")
                            .param("targetId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.content[0].content").value("Good!"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/comments — 创建评论")
    class CreateComment {

        @Test
        @DisplayName("有效请求 + JWT userId — 返回 200 + 新建评论")
        void validRequest_returnsCreatedComment() throws Exception {
            CommentRequest req = new CommentRequest();
            req.setContent("好问题！");

            var resp = commentResponse(1L, "好问题！", "author");
            when(commentService.create(eq(TargetType.QUESTION), eq(1L), eq(1L), any(CommentRequest.class)))
                    .thenReturn(resp);

            mockMvc.perform(post("/api/v1/comments")
                            .param("targetType", "QUESTION")
                            .param("targetId", "1")
                            .requestAttr("userId", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value("好问题！"));

            verify(commentService).create(eq(TargetType.QUESTION), eq(1L), eq(1L), any(CommentRequest.class));
        }

        @Test
        @DisplayName("空内容 — 返回 400")
        void emptyContent_returnsBadRequest() throws Exception {
            CommentRequest req = new CommentRequest();
            req.setContent("");

            mockMvc.perform(post("/api/v1/comments")
                            .param("targetType", "QUESTION")
                            .param("targetId", "1")
                            .requestAttr("userId", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/comments/{id} — 删除评论")
    class DeleteComment {

        @Test
        @DisplayName("作者删除 — 返回 200")
        void authorDeletes_returnsOk() throws Exception {
            doNothing().when(commentService).delete(1L, 1L);

            mockMvc.perform(delete("/api/v1/comments/1").requestAttr("userId", 1L))
                    .andExpect(status().isOk());

            verify(commentService).delete(1L, 1L);
        }
    }

    // -- fixtures --

    private com.yiyixing.dto.response.CommentResponse commentResponse(
            Long id, String content, String username) {
        var resp = new com.yiyixing.dto.response.CommentResponse();
        resp.setId(id);
        resp.setContent(content);
        var brief = new com.yiyixing.dto.response.UserBrief();
        brief.setId(1L);
        brief.setUsername(username);
        resp.setAuthor(brief);
        resp.setCreatedAt(Instant.now());
        resp.setUpdatedAt(Instant.now());
        return resp;
    }
}
