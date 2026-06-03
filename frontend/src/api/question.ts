import request from './request';
import type {
  ApiResponse,
  Question,
  QuestionRequest,
  Answer,
  AnswerRequest,
} from '@/types';

export function listQuestions(params: { page?: number; size?: number; sort?: string }) {
  return request.get<ApiResponse<{ content: Question[]; totalElements: number; totalPages: number }>>(
    '/questions', { params }
  );
}

export function getQuestion(id: number) {
  return request.get<ApiResponse<Question>>(`/questions/${id}`);
}

export function createQuestion(data: QuestionRequest) {
  return request.post<ApiResponse<Question>>('/questions', data);
}

export function deleteQuestion(id: number) {
  return request.delete<ApiResponse<null>>(`/questions/${id}`);
}

export function listAnswers(questionId: number) {
  return request.get<ApiResponse<Answer[]>>(`/questions/${questionId}/answers`);
}

export function createAnswer(questionId: number, data: AnswerRequest) {
  return request.post<ApiResponse<Answer>>(`/questions/${questionId}/answers`, data);
}

export function acceptAnswer(questionId: number, answerId: number) {
  return request.put<ApiResponse<Answer>>(`/questions/${questionId}/accept/${answerId}`);
}

export function deleteAnswer(questionId: number, answerId: number) {
  return request.delete<ApiResponse<null>>(`/questions/${questionId}/answers/${answerId}`);
}
