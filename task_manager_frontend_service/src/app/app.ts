import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { catchError, forkJoin, of, timeout } from 'rxjs';

import { CreateDrawerComponent } from './components/create-drawer/create-drawer.component';
import { RequestModalComponent } from './components/request-modal/request-modal.component';
import { RequestTableComponent } from './components/request-table/request-table.component';
import { ReportsAnalyticsComponent } from './components/reports-analytics/reports-analytics.component';
import { SummaryCardsComponent } from './components/summary-cards/summary-cards.component';
import { TopbarComponent } from './components/topbar/topbar.component';
import { BUSINESS_SERVICE_ORIGIN } from './core/api.config';
import { BusinessApiService } from './core/business-api.service';
import {
  ApiErrorResponse,
  Task,
  TaskComment,
  TaskPriority,
  TaskRequestPayload,
  TaskStatus,
  Team,
  User
} from './core/business-api.types';
import {
  CommentViewModel,
  CreateTaskFormModel,
  QueueFilter,
  RequestModalContext,
  SortDirection,
  TaskSortField
} from './shared/dashboard.types';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    TopbarComponent,
    SummaryCardsComponent,
    RequestTableComponent,
    ReportsAnalyticsComponent,
    RequestModalComponent,
    CreateDrawerComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  private readonly themeStorageKey = 'task-manager-theme';
  private readonly api = inject(BusinessApiService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly requestTimeoutMs = 8000;
  // Temporary stand-in until auth/user context is wired in.
  private readonly currentUserId = 1;

  protected readonly apiBaseUrl = BUSINESS_SERVICE_ORIGIN;
  protected readonly filters: QueueFilter[] = [
    'All requests',
    'Open requests',
    'My queue',
    'Unassigned',
    'Breaching SLA',
    'Cancelled'
  ];
  protected readonly statuses: TaskStatus[] = ['OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];
  protected readonly priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
  protected readonly pageSizeOptions = [10, 20, 30, 50];

  protected tasks: Task[] = [];
  protected users: User[] = [];
  protected teams: Team[] = [];
  protected comments: TaskComment[] = [];

  protected isLoading = true;
  protected isTaskListRefreshing = false;
  protected isSaving = false;
  protected isRefreshingComments = false;
  protected isCreateDrawerOpen = false;
  protected errorMessage = '';
  protected activeFilter: QueueFilter = 'All requests';
  protected activeNavigation = 'Requests';
  protected theme: 'light' | 'dark' = 'light';
  protected selectedTaskId: number | null = null;
  protected assignmentUserId: number | null = null;
  protected assignmentTeamId: number | null = null;
  protected isEditingDetails = false;
  protected detailTitle = '';
  protected detailDescription = '';
  protected detailPriority: TaskPriority = 'MEDIUM';
  protected editingCommentId: number | null = null;
  protected editingCommentText = '';
  protected newCommentText = '';
  protected activeSortField: TaskSortField = 'updated';
  protected activeSortDirection: SortDirection = 'desc';
  protected currentPage = 1;
  protected pageSize = 10;

  protected readonly createForm: CreateTaskFormModel = {
    title: '',
    description: '',
    assignedUserId: null,
    assignedTeamId: null,
    status: 'OPEN',
    priority: 'MEDIUM',
    dueDate: ''
  };

  ngOnInit(): void {
    this.initializeTheme();
    this.loadDashboard();
  }

  protected get summaryCards() {
    const openRequests = this.tasks.filter(task => task.status !== 'COMPLETED' && task.status !== 'CANCELLED');
    const breached = openRequests.filter(task => this.isBreached(task));
    const resolved = this.tasks.filter(task => task.status === 'COMPLETED');
    const onlineTeams = this.teams.filter(team => this.teamLoad.find(item => item.team.id === team.id)?.openCount).length;

    return [
      {
        label: 'Open requests',
        value: String(openRequests.length),
        detail: `${this.tasks.filter(task => task.status === 'OPEN').length} still untouched`,
        tone: 'amber' as const
      },
      {
        label: 'Due in SLA breach window',
        value: String(breached.length),
        detail: breached.length ? 'Needs intervention now' : 'No breaches detected',
        tone: 'rose' as const
      },
      {
        label: 'Resolved tasks',
        value: String(resolved.length),
        detail: `${this.tasks.length ? Math.round((resolved.length / this.tasks.length) * 100) : 0}% of total volume`,
        tone: 'teal' as const
      },
      {
        label: 'Teams with workload',
        value: String(onlineTeams),
        detail: `${this.users.length} users available in the directory`,
        tone: 'ink' as const
      }
    ];
  }

  protected get filteredTickets(): Task[] {
    switch (this.activeFilter) {
      case 'Open requests':
        return this.tasks.filter(task => task.status === 'OPEN' || task.status === 'IN_PROGRESS');
      case 'My queue':
        return this.tasks.filter(task => task.assignedUserId === this.currentUserId);
      case 'Unassigned':
        return this.tasks.filter(task => task.assignedUserId === null);
      case 'Breaching SLA':
        return this.tasks.filter(task => this.isBreached(task));
      case 'Cancelled':
        return this.tasks.filter(task => task.status === 'CANCELLED');
      default:
        return this.tasks;
    }
  }

  protected get unreadTasks(): Task[] {
    return this.tasks
      .filter(task => task.assignedUserId === this.currentUserId && task.readStatus === 'UNREAD')
      .sort((left, right) => new Date(right.updatedAt ?? right.createdAt ?? 0).getTime() - new Date(left.updatedAt ?? left.createdAt ?? 0).getTime());
  }

  protected get selectedTask(): Task | null {
    return this.tasks.find(task => task.id === this.selectedTaskId) ?? null;
  }

  protected get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredTickets.length / this.pageSize));
  }

  protected get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, index) => index + 1);
  }

  protected get currentPageTickets(): Task[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredTickets.slice(startIndex, startIndex + this.pageSize);
  }

  protected get selectedUser(): User | null {
    const task = this.selectedTask;
    if (!task?.assignedUserId) {
      return null;
    }
    return this.users.find(user => user.id === task.assignedUserId) ?? null;
  }

  protected get selectedTeam(): Team | null {
    const task = this.selectedTask;
    if (!task?.assignedTeamId) {
      return null;
    }
    return this.teams.find(team => team.id === task.assignedTeamId) ?? null;
  }

  protected get teamLoad() {
    return this.teams.map(team => {
      const assignedTasks = this.tasks.filter(task => task.assignedTeamId === team.id);
      return {
        team,
        openCount: assignedTasks.length,
        breachedCount: assignedTasks.filter(task => this.isBreached(task)).length,
        width: Math.min(100, assignedTasks.length * 12)
      };
    }).sort((left, right) => right.openCount - left.openCount);
  }

  protected get commentViewModels(): CommentViewModel[] {
    return this.comments.map(comment => ({
      ...comment,
      username: this.users.find(user => user.id === comment.userId)?.username ?? `User #${comment.userId}`,
      meta: `${this.formatDateTime(comment.createdAt)} - ${this.users.find(user => user.id === comment.userId)?.username ?? `User #${comment.userId}`}`
    }));
  }

  protected get modalContext(): RequestModalContext | null {
    const task = this.selectedTask;
    if (!task) {
      return null;
    }

    return {
      task,
      selectedUser: this.selectedUser,
      selectedTeam: this.selectedTeam,
      comments: this.commentViewModels,
      users: this.users,
      teams: this.teams,
      statuses: this.statuses,
      priorities: this.priorities,
      isSaving: this.isSaving,
      isRefreshingComments: this.isRefreshingComments,
      isEditingDetails: this.isEditingDetails,
      detailTitle: this.detailTitle,
      detailDescription: this.detailDescription,
      detailPriority: this.detailPriority,
      assignmentUserId: this.assignmentUserId,
      assignmentTeamId: this.assignmentTeamId,
      editingCommentId: this.editingCommentId,
      editingCommentText: this.editingCommentText,
      newCommentText: this.newCommentText
    };
  }

  protected loadDashboard(preferredTaskId?: number): void {
    this.isLoading = true;
    this.errorMessage = '';

    forkJoin({
      tasks: this.api.getTasks(this.activeSortField, this.activeSortDirection).pipe(
        timeout(this.requestTimeoutMs),
        catchError(error => {
          this.appendErrorMessage(`Tasks failed: ${this.extractErrorMessage(error)}`);
          return of([] as Task[]);
        })
      ),
      users: this.api.getUsers().pipe(
        timeout(this.requestTimeoutMs),
        catchError(error => {
          this.appendErrorMessage(`Users failed: ${this.extractErrorMessage(error)}`);
          return of([] as User[]);
        })
      ),
      teams: this.api.getTeams().pipe(
        timeout(this.requestTimeoutMs),
        catchError(error => {
          this.appendErrorMessage(`Teams failed: ${this.extractErrorMessage(error)}`);
          return of([] as Team[]);
        })
      )
    }).subscribe({
      next: ({ tasks, users, teams }) => {
        this.tasks = tasks;
        this.users = users;
        this.teams = teams;
        this.isLoading = false;
        this.currentPage = Math.min(this.currentPage, this.totalPages);

        const fallbackTaskId = preferredTaskId ?? this.selectedTaskId ?? null;
        if (fallbackTaskId && this.tasks.some(task => task.id === fallbackTaskId)) {
          this.selectTicket(fallbackTaskId);
        } else {
          this.closeTicketModal();
        }
        this.cdr.detectChanges();
      },
      error: error => {
        this.isLoading = false;
        this.errorMessage = this.extractErrorMessage(error);
        this.cdr.detectChanges();
      }
    });
  }

  protected refreshDashboard(): void {
    this.refreshTasks(this.selectedTaskId ?? undefined, true);
  }

  protected changeSort(field: TaskSortField): void {
    if (this.activeSortField === field) {
      this.activeSortDirection = this.activeSortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.activeSortField = field;
      this.activeSortDirection = field === 'updated' || field === 'created' || field === 'due' ? 'desc' : 'asc';
    }

    this.currentPage = 1;
    this.refreshTasks(this.selectedTaskId ?? undefined);
  }

  protected selectTicket(taskId: number): void {
    this.selectedTaskId = taskId;
    const task = this.selectedTask;
    this.assignmentUserId = task?.assignedUserId ?? null;
    this.assignmentTeamId = task?.assignedTeamId ?? null;
    this.isEditingDetails = false;
    this.detailTitle = task?.title ?? '';
    this.detailDescription = task?.description ?? '';
    this.detailPriority = task?.priority ?? 'MEDIUM';
    this.editingCommentId = null;
    this.editingCommentText = '';

    if (task) {
      this.markSelectedTaskAsRead(task.id);
      this.loadComments(task.id);
    }
  }

  protected closeTicketModal(): void {
    this.selectedTaskId = null;
    this.comments = [];
    this.assignmentUserId = null;
    this.assignmentTeamId = null;
    this.isEditingDetails = false;
    this.detailTitle = '';
    this.detailDescription = '';
    this.detailPriority = 'MEDIUM';
    this.editingCommentId = null;
    this.editingCommentText = '';
  }

  protected setFilter(filter: QueueFilter): void {
    this.activeFilter = filter;
    this.currentPage = 1;
  }

  protected setNavigation(label: string): void {
    this.activeNavigation = label;
    if (label !== 'Requests') {
      this.closeTicketModal();
    }
  }

  protected setPageSize(pageSize: number): void {
    this.pageSize = pageSize;
    this.currentPage = 1;
  }

  protected setCurrentPage(page: number): void {
    this.currentPage = Math.min(Math.max(page, 1), this.totalPages);
  }

  protected openTaskFromAlert(taskId: number): void {
    this.activeNavigation = 'Requests';
    this.selectTicket(taskId);
  }

  protected setTheme(theme: 'light' | 'dark'): void {
    if (this.theme === theme) {
      return;
    }

    this.theme = theme;
    this.applyTheme(theme);
    localStorage.setItem(this.themeStorageKey, theme);
  }

  protected setAssignmentUserId(userId: number | null): void {
    this.assignmentUserId = userId;
  }

  protected setAssignmentTeamId(teamId: number | null): void {
    this.assignmentTeamId = teamId;
  }

  protected setDetailTitle(value: string): void {
    this.detailTitle = value;
  }

  protected setDetailDescription(value: string): void {
    this.detailDescription = value;
  }

  protected setDetailPriority(value: TaskPriority): void {
    this.detailPriority = value;
  }

  protected beginDetailsEdit(): void {
    const task = this.selectedTask;
    if (!task) {
      return;
    }

    this.isEditingDetails = true;
    this.detailTitle = task.title;
    this.detailDescription = task.description ?? '';
    this.detailPriority = task.priority ?? 'MEDIUM';
  }

  protected cancelDetailsEdit(): void {
    const task = this.selectedTask;
    this.isEditingDetails = false;
    this.detailTitle = task?.title ?? '';
    this.detailDescription = task?.description ?? '';
    this.detailPriority = task?.priority ?? 'MEDIUM';
  }

  protected setEditingCommentText(value: string): void {
    this.editingCommentText = value;
  }

  protected setNewCommentText(value: string): void {
    this.newCommentText = value;
  }

  protected updateStatus(status: TaskStatus): void {
    const task = this.selectedTask;
    if (!task) {
      return;
    }

    this.persistTaskUpdate(task.id, { status });
  }

  protected saveAssignment(): void {
    const task = this.selectedTask;
    if (!task) {
      return;
    }

    const payload: TaskRequestPayload = {
      assignedUserId: this.assignmentUserId,
      assignedTeamId: this.assignmentTeamId
    };

    this.persistTaskUpdate(task.id, payload);
  }

  protected saveDetails(): void {
    const task = this.selectedTask;
    if (!task) {
      return;
    }

    if (!this.detailTitle.trim()) {
      this.errorMessage = 'Title is required.';
      return;
    }

    const payload: TaskRequestPayload = {
      title: this.detailTitle.trim(),
      description: this.detailDescription.trim() || null,
      priority: this.detailPriority,
      assignedUserId: this.assignmentUserId,
      assignedTeamId: this.assignmentTeamId
    };

    this.persistTaskUpdate(task.id, payload);
  }

  protected toggleCreateDrawer(): void {
    this.isCreateDrawerOpen = !this.isCreateDrawerOpen;
    this.errorMessage = '';
  }

  protected submitCreateTask(): void {
    if (!this.createForm.title.trim()) {
      this.errorMessage = 'Title is required before creating a request.';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    const payload: TaskRequestPayload = {
      title: this.createForm.title.trim(),
      description: this.createForm.description.trim() || null,
      assignedUserId: this.createForm.assignedUserId,
      assignedTeamId: this.createForm.assignedTeamId,
      status: this.createForm.status,
      priority: this.createForm.priority,
      dueDate: this.createForm.dueDate ? new Date(this.createForm.dueDate).toISOString() : null
    };

    this.api.createTask(payload).subscribe({
      next: task => {
        this.isSaving = false;
        this.isCreateDrawerOpen = false;
        this.resetCreateForm();
        this.loadDashboard(task.id);
      },
      error: error => {
        this.isSaving = false;
        this.errorMessage = this.extractErrorMessage(error);
      }
    });
  }

  protected beginCommentEdit(comment: TaskComment): void {
    this.editingCommentId = comment.id;
    this.editingCommentText = comment.commentText;
  }

  protected cancelCommentEdit(): void {
    this.editingCommentId = null;
    this.editingCommentText = '';
  }

  protected saveCommentEdit(commentId: number): void {
    if (!this.editingCommentText.trim()) {
      this.errorMessage = 'Comment text is required.';
      return;
    }

    this.isSaving = true;
    this.api.updateComment(commentId, this.editingCommentText.trim()).subscribe({
      next: () => {
        this.isSaving = false;
        this.cancelCommentEdit();
        if (this.selectedTask) {
          this.loadComments(this.selectedTask.id);
        }
      },
      error: error => {
        this.isSaving = false;
        this.errorMessage = this.extractErrorMessage(error);
      }
    });
  }

  protected createComment(): void {
    const task = this.selectedTask;
    if (!task) {
      return;
    }

    if (!this.newCommentText.trim()) {
      this.errorMessage = 'Comment text is required.';
      return;
    }

    this.isSaving = true;
    this.api.createComment({
      taskId: task.id,
      userId: this.currentUserId,
      commentText: this.newCommentText.trim()
    }).subscribe({
      next: () => {
        this.isSaving = false;
        this.newCommentText = '';
        this.loadComments(task.id);
      },
      error: error => {
        this.isSaving = false;
        this.errorMessage = this.extractErrorMessage(error);
      }
    });
  }

  protected deleteComment(commentId: number): void {
    if (!window.confirm('Delete this comment?')) {
      return;
    }

    this.isSaving = true;
    this.api.deleteComment(commentId).subscribe({
      next: () => {
        this.isSaving = false;
        if (this.selectedTask) {
          this.loadComments(this.selectedTask.id);
        }
      },
      error: error => {
        this.isSaving = false;
        this.errorMessage = this.extractErrorMessage(error);
      }
    });
  }

  protected getUserName(userId: number | null): string {
    if (userId === null) {
      return 'Unassigned';
    }
    return this.users.find(user => user.id === userId)?.username ?? `User #${userId}`;
  }

  protected getTeamName(teamId: number | null): string {
    if (teamId === null) {
      return 'No team';
    }
    return this.teams.find(team => team.id === teamId)?.name ?? `Team #${teamId}`;
  }

  protected statusLabel(status: TaskStatus | null): string {
    return status ? status.replace('_', ' ') : 'UNSET';
  }

  protected statusClass(status: TaskStatus | null): string {
    return `status-${(status ?? 'OPEN').toLowerCase()}`;
  }

  protected priorityClass(priority: TaskPriority | null): string {
    return `priority-${(priority ?? 'LOW').toLowerCase()}`;
  }

  protected formatDateTime(value: string | null): string {
    if (!value) {
      return 'No timestamp';
    }

    return new Intl.DateTimeFormat('en-US', {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit'
    }).format(new Date(value));
  }

  protected formatDueDate(value: string | null): string {
    if (!value) {
      return 'No due date';
    }

    return new Intl.DateTimeFormat('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: 'numeric',
      minute: '2-digit'
    }).format(new Date(value));
  }

  private loadComments(taskId: number): void {
    this.isRefreshingComments = true;
    this.api.getCommentsByTaskId(taskId).pipe(
      timeout(this.requestTimeoutMs)
    ).subscribe({
      next: comments => {
        this.comments = comments;
        this.isRefreshingComments = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.comments = [];
        this.isRefreshingComments = false;
        this.errorMessage = this.extractErrorMessage(error);
        this.cdr.detectChanges();
      }
    });
  }

  private refreshTasks(preferredTaskId?: number, forceLoadingState = false): void {
    if (forceLoadingState) {
      this.isLoading = true;
    } else {
      this.isTaskListRefreshing = true;
    }

    this.errorMessage = '';

    this.api.getTasks(this.activeSortField, this.activeSortDirection).pipe(
      timeout(this.requestTimeoutMs),
      catchError(error => {
        this.appendErrorMessage(`Tasks failed: ${this.extractErrorMessage(error)}`);
        return of([] as Task[]);
      })
    ).subscribe({
      next: tasks => {
        this.tasks = tasks;
        this.isLoading = false;
        this.isTaskListRefreshing = false;
        this.currentPage = Math.min(this.currentPage, this.totalPages);

        const fallbackTaskId = preferredTaskId ?? this.selectedTaskId ?? null;
        if (fallbackTaskId && this.tasks.some(task => task.id === fallbackTaskId)) {
          const task = this.tasks.find(item => item.id === fallbackTaskId) ?? null;
          this.selectedTaskId = task?.id ?? null;
          this.assignmentUserId = task?.assignedUserId ?? null;
          this.assignmentTeamId = task?.assignedTeamId ?? null;
        } else {
          this.closeTicketModal();
        }

        this.cdr.detectChanges();
      },
      error: error => {
        this.isLoading = false;
        this.isTaskListRefreshing = false;
        this.errorMessage = this.extractErrorMessage(error);
        this.cdr.detectChanges();
      }
    });
  }

  private persistTaskUpdate(taskId: number, payload: TaskRequestPayload): void {
    this.isSaving = true;
    this.errorMessage = '';

    this.api.updateTask(taskId, payload).subscribe({
      next: updatedTask => {
        this.isSaving = false;
        this.isEditingDetails = false;
        this.tasks = this.tasks.map(task => task.id === updatedTask.id ? updatedTask : task);
        this.selectTicket(updatedTask.id);
      },
      error: error => {
        this.isSaving = false;
        this.errorMessage = this.extractErrorMessage(error);
      }
    });
  }

  private markSelectedTaskAsRead(taskId: number): void {
    this.api.markTaskAsRead(taskId, this.currentUserId).subscribe({
      next: updatedTask => {
        this.tasks = this.tasks.map(task => task.id === updatedTask.id ? updatedTask : task);
        if (this.selectedTaskId === updatedTask.id) {
          this.assignmentUserId = updatedTask.assignedUserId;
          this.assignmentTeamId = updatedTask.assignedTeamId;
        }
        this.cdr.detectChanges();
      },
      error: () => {
        // Ignore read-marker failures so opening the task never blocks the detail view.
      }
    });
  }

  private extractErrorMessage(error: unknown): string {
    const maybeError = error as { error?: ApiErrorResponse | string };

    if (typeof maybeError?.error === 'string') {
      return maybeError.error;
    }

    if (maybeError?.error && typeof maybeError.error === 'object') {
      const response = maybeError.error as ApiErrorResponse;
      if (response.validationErrors) {
        return Object.entries(response.validationErrors)
          .map(([field, message]) => `${field}: ${message}`)
          .join(' | ');
      }
      return response.message || 'Request failed.';
    }

    return 'Request failed. Check that the business service is running and the Angular proxy is active.';
  }

  private appendErrorMessage(message: string): void {
    if (!this.errorMessage) {
      this.errorMessage = message;
      this.cdr.detectChanges();
      return;
    }

    if (!this.errorMessage.includes(message)) {
      this.errorMessage = `${this.errorMessage} | ${message}`;
      this.cdr.detectChanges();
    }
  }

  private isBreached(task: Task): boolean {
    if (!task.dueDate || task.status === 'COMPLETED' || task.status === 'CANCELLED') {
      return false;
    }
    return new Date(task.dueDate).getTime() < Date.now();
  }

  private initializeTheme(): void {
    const storedTheme = localStorage.getItem(this.themeStorageKey);
    const theme = storedTheme === 'dark' ? 'dark' : 'light';

    this.theme = theme;
    this.applyTheme(theme);
  }

  private applyTheme(theme: 'light' | 'dark'): void {
    document.body.dataset['theme'] = theme;
    document.documentElement.style.colorScheme = theme;
  }

  private resetCreateForm(): void {
    this.createForm.title = '';
    this.createForm.description = '';
    this.createForm.assignedUserId = null;
    this.createForm.assignedTeamId = null;
    this.createForm.status = 'OPEN';
    this.createForm.priority = 'MEDIUM';
    this.createForm.dueDate = '';
  }
}
