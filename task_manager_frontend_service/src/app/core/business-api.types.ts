export type TaskStatus = 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
export type TaskReadStatus = 'READ' | 'UNREAD';

export interface Task {
  id: number;
  assignedUserId: number | null;
  assignedTeamId: number | null;
  title: string;
  description: string | null;
  status: TaskStatus | null;
  priority: TaskPriority | null;
  readStatus: TaskReadStatus;
  dueDate: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface User {
  id: number;
  teamId: number | null;
  username: string;
  email: string;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface Team {
  id: number;
  name: string;
  description: string | null;
  teamLeadId: number | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface TaskComment {
  id: number;
  taskId: number;
  userId: number;
  commentText: string;
  createdAt: string | null;
}

export interface TaskRequestPayload {
  assignedUserId?: number | null;
  assignedTeamId?: number | null;
  title?: string;
  description?: string | null;
  status?: TaskStatus | null;
  priority?: TaskPriority | null;
  dueDate?: string | null;
}

export interface TaskCommentRequestPayload {
  taskId: number;
  userId: number;
  commentText: string;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors: Record<string, string> | null;
}
