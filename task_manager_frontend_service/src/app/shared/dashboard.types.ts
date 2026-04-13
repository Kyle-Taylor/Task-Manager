import { Task, TaskComment, TaskPriority, TaskStatus, Team, User } from '../core/business-api.types';

export type QueueFilter = 'All requests' | 'Open requests' | 'My queue' | 'Unassigned' | 'Breaching SLA' | 'Cancelled';
export type TaskSortField = 'id' | 'status' | 'priority' | 'team' | 'assignee' | 'due' | 'updated' | 'created';
export type SortDirection = 'asc' | 'desc';

export interface NavigationItem {
  label: string;
  count: number | null;
  active: boolean;
}

export interface SummaryCard {
  label: string;
  value: string;
  detail: string;
  tone: 'amber' | 'rose' | 'teal' | 'ink';
}

export interface TeamLoadItem {
  team: Team;
  openCount: number;
  breachedCount: number;
  width: number;
}

export interface CommentViewModel extends TaskComment {
  username: string;
  meta: string;
}

export interface CreateTaskFormModel {
  title: string;
  description: string;
  assignedUserId: number | null;
  assignedTeamId: number | null;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate: string;
}

export interface RequestModalContext {
  task: Task;
  selectedUser: User | null;
  selectedTeam: Team | null;
  comments: CommentViewModel[];
  users: User[];
  teams: Team[];
  statuses: TaskStatus[];
  priorities: TaskPriority[];
  isSaving: boolean;
  isRefreshingComments: boolean;
  isEditingDetails: boolean;
  detailTitle: string;
  detailDescription: string;
  detailPriority: TaskPriority;
  assignmentUserId: number | null;
  assignmentTeamId: number | null;
  editingCommentId: number | null;
  editingCommentText: string;
  newCommentText: string;
}
