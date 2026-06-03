package com.yiyixing.service;

import com.yiyixing.dto.request.AnswerRequest;
import com.yiyixing.dto.response.AnswerResponse;
import com.yiyixing.entity.Answer;
import com.yiyixing.entity.Question;
import com.yiyixing.entity.User;
import com.yiyixing.exception.ApiException;
import com.yiyixing.repository.AnswerRepository;
import com.yiyixing.repository.QuestionRepository;
import com.yiyixing.repository.UserRepository;
import com.yiyixing.util.MarkdownUtil;
import com.yiyixing.service.impl.AnswerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MarkdownUtil markdownUtil;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private Question question;
    private User author;
    private Answer answer;

    @Nested
    @DisplayName("创建回答")
    class Create {

        @Test
        @DisplayName("成功创建回答 — Markdown 渲染 + 计数器递增")
        void success_rendersMarkdownAndIncrementsCount() {
            answerService = new AnswerServiceImpl(answerRepository, questionRepository, userRepository, markdownUtil);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(question()));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
            when(markdownUtil.render("hello")).thenReturn("<p>hello</p>");
            when(answerRepository.save(any(Answer.class))).thenAnswer(inv -> inv.getArgument(0));

            AnswerRequest req = new AnswerRequest();
            req.setContent("hello");
            AnswerResponse resp = answerService.create(1L, 1L, req);

            assertNotNull(resp);
            assertEquals("<p>hello</p>", resp.getContentHtml());
            verify(answerRepository).save(any());
            verify(questionRepository).incrementAnswerCount(1L);
        }

        @Test
        @DisplayName("问题不存在 — 抛 404")
        void questionNotFound_throwsNotFound() {
            answerService = new AnswerServiceImpl(answerRepository, questionRepository, userRepository, markdownUtil);
            when(questionRepository.findById(99L)).thenReturn(Optional.empty());

            AnswerRequest req = new AnswerRequest();
            req.setContent("x");
            ApiException ex = assertThrows(ApiException.class,
                    () -> answerService.create(99L, 1L, req));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            assertEquals("问题不存在", ex.getMessage());
        }

        @Test
        @DisplayName("用户不存在 — 抛 404")
        void userNotFound_throwsNotFound() {
            answerService = new AnswerServiceImpl(answerRepository, questionRepository, userRepository, markdownUtil);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(question()));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            AnswerRequest req = new AnswerRequest();
            req.setContent("x");
            ApiException ex = assertThrows(ApiException.class,
                    () -> answerService.create(1L, 99L, req));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("获取回答")
    class GetByQuestion {

        @Test
        @DisplayName("按创建时间升序返回")
        void success_returnsAscendingOrder() {
            when(answerRepository.findByQuestionIdOrderByCreatedAtAsc(1L))
                    .thenReturn(List.of(answer()));

            var result = answerService.getByQuestionId(1L);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("采纳回答")
    class Accept {

        @Test
        @DisplayName("问题作者采纳回答 — 成功")
        void success_marksAnswerAndResolvesQuestion() {
            answerService = new AnswerServiceImpl(answerRepository, questionRepository, userRepository, markdownUtil);
            Question q = question();
            Answer a = answerFor(q);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(q));
            when(answerRepository.findById(2L)).thenReturn(Optional.of(a));
            when(answerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(questionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AnswerResponse resp = answerService.accept(1L, 2L, 1L);

            assertTrue(resp.getAccepted());
            verify(answerRepository).save(any());
            verify(questionRepository).save(argThat(savedQ -> savedQ.getStatus() == Question.Status.RESOLVED));
        }

        @Test
        @DisplayName("非问题作者 — 抛 403")
        void notAuthor_throwsForbidden() {
            answerService = new AnswerServiceImpl(answerRepository, questionRepository, userRepository, markdownUtil);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(question()));

            ApiException ex = assertThrows(ApiException.class,
                    () -> answerService.accept(1L, 2L, 999L));
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
            assertEquals("只有问题作者可以采纳回答", ex.getMessage());
        }

        @Test
        @DisplayName("回答不属于该问题 — 抛 400")
        void answerNotForQuestion_throwsBadRequest() {
            answerService = new AnswerServiceImpl(answerRepository, questionRepository, userRepository, markdownUtil);
            Answer wrongAnswer = new Answer();
            wrongAnswer.setId(2L);
            Question otherQ = new Question();
            otherQ.setId(88L);
            wrongAnswer.setQuestion(otherQ);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(question()));
            when(answerRepository.findById(2L)).thenReturn(Optional.of(wrongAnswer));

            ApiException ex = assertThrows(ApiException.class,
                    () -> answerService.accept(1L, 2L, 1L));
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("删除回答")
    class Delete {

        @Test
        @DisplayName("作者删除自己的回答 — 成功")
        void authorDeletes_success() {
            when(answerRepository.findById(1L)).thenReturn(Optional.of(answer()));

            assertDoesNotThrow(() -> answerService.delete(1L, 1L));
            verify(answerRepository).delete(any());
        }

        @Test
        @DisplayName("非作者删除 — 抛 403")
        void notAuthor_throwsForbidden() {
            when(answerRepository.findById(1L)).thenReturn(Optional.of(answer()));

            ApiException ex = assertThrows(ApiException.class,
                    () -> answerService.delete(1L, 999L));
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        }
    }

    // -- fixtures --

    private Question question() {
        Question q = new Question();
        q.setId(1L);
        q.setStatus(Question.Status.OPEN);
        User u = new User();
        u.setId(1L);
        q.setAuthor(u);
        return q;
    }

    private User user() {
        User u = new User();
        u.setId(1L);
        return u;
    }

    private Answer answer() {
        Answer a = new Answer();
        a.setId(1L);
        a.setContent("test");
        a.setAccepted(false);
        User u = new User();
        u.setId(1L);
        a.setAuthor(u);
        return a;
    }

    private Answer answerFor(Question q) {
        Answer a = new Answer();
        a.setId(2L);
        a.setQuestion(q);
        a.setAccepted(false);
        User u = new User();
        u.setId(1L);
        a.setAuthor(u);
        return a;
    }
}
