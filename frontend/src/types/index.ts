export interface User {
  id: number;
  username: string;
  email: string;
  avatarUrl: string | null;
  bio: string | null;
  reputation: number;
  role: string;
  createdAt: string;
}

export interface UserBrief {
  id: number;
  username: string;
  avatarUrl: string | null;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

// --- Question ---

export interface Tag {
  id: number;
  name: string;
  description: string | null;
  useCount: number;
  createdAt: string;
}

export interface Question {
  id: number;
  title: string;
  content: string | null;
  contentHtml: string | null;
  viewCount: number;
  answerCount: number;
  status: string;
  author: UserBrief;
  tags: Tag[];
  createdAt: string;
  updatedAt: string;
}

export interface QuestionRequest {
  title: string;
  content: string;
  tags: string[];
}

// --- Answer ---

export interface Answer {
  id: number;
  content: string;
  contentHtml: string;
  accepted: boolean;
  author: UserBrief;
  createdAt: string;
  updatedAt: string;
}

export interface AnswerRequest {
  content: string;
}

// --- Comment ---

export interface Comment {
  id: number;
  content: string;
  author: UserBrief;
  createdAt: string;
  updatedAt: string;
}

export type CommentTargetType = 'QUESTION' | 'ANSWER' | 'ARTICLE';

export interface CommentRequest {
  content: string;
}

// --- Article ---

export interface Article {
  id: number;
  title: string;
  content: string | null;
  contentHtml: string | null;
  slug: string;
  status: string;
  viewCount: number;
  likeCount: number;
  favoriteCount: number;
  commentCount: number;
  author: UserBrief;
  tags: Tag[];
  createdAt: string;
  updatedAt: string;
  publishedAt: string | null;
}

export interface ArticleRequest {
  title: string;
  content: string;
  tags: string[];
}

// --- Interaction ---

export interface InteractionResponse {
  liked: boolean;
  count: number;
}

export type InteractionTargetType = 'QUESTION' | 'ANSWER' | 'ARTICLE' | 'COMMENT';

// --- Follow ---

export interface FollowData {
  id: number;
  follower: UserBrief;
  following: UserBrief;
  createdAt: string;
}

// --- Notification ---

export interface Notification {
  id: number;
  type: string;
  fromUser: UserBrief | null;
  targetType: string | null;
  targetId: number | null;
  content: string | null;
  read: boolean;
  createdAt: string;
}
