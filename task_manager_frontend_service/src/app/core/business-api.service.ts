import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { API_BASE_URL } from './api.config';
import { Task, TaskComment, TaskCommentRequestPayload, TaskRequestPayload, Team, User, UserRequestPayload } from './business-api.types';
import { SortDirection, TaskSortField } from '../shared/dashboard.types';

@Injectable({ providedIn: 'root' })
export class BusinessApiService {
  private readonly http = inject(HttpClient);

  getTasks(sortBy: TaskSortField = 'updated', direction: SortDirection = 'desc') {
    return this.http.get<Task[]>(`${API_BASE_URL}/tasks`, {
      params: {
        sortBy,
        direction
      }
    });
  }

  getUsers() {
    return this.http.get<User[]>(`${API_BASE_URL}/profiles/users`);
  }

  createUser(payload: UserRequestPayload) {
    return this.http.post<User>(`${API_BASE_URL}/profiles/users`, payload);
  }

  getTeams() {
    return this.http.get<Team[]>(`${API_BASE_URL}/profiles/teams`);
  }

  getCommentsByTaskId(taskId: number) {
    return this.http.get<TaskComment[]>(`${API_BASE_URL}/comments/task/${taskId}`);
  }

  createTask(payload: TaskRequestPayload) {
    return this.http.post<Task>(`${API_BASE_URL}/tasks`, payload);
  }

  createComment(payload: TaskCommentRequestPayload) {
    return this.http.post<TaskComment>(`${API_BASE_URL}/comments`, payload);
  }

  updateTask(taskId: number, payload: TaskRequestPayload) {
    return this.http.patch<Task>(`${API_BASE_URL}/tasks/${taskId}`, payload);
  }

  markTaskAsRead(taskId: number, viewerUserId: number) {
    return this.http.patch<Task>(`${API_BASE_URL}/tasks/${taskId}/read`, null, {
      params: { viewerUserId }
    });
  }

  updateComment(commentId: number, commentText: string) {
    return this.http.patch<TaskComment>(`${API_BASE_URL}/comments/${commentId}`, commentText, {
      headers: { 'Content-Type': 'text/plain' }
    });
  }

  deleteComment(commentId: number) {
    return this.http.delete<void>(`${API_BASE_URL}/comments/${commentId}`);
  }
}
