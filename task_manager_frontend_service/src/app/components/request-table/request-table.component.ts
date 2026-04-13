import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Task, TaskPriority, TaskStatus } from '../../core/business-api.types';
import { QueueFilter, SortDirection, TaskSortField } from '../../shared/dashboard.types';

@Component({
  selector: 'app-request-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './request-table.component.html',
  styleUrl: './request-table.component.scss'
})
export class RequestTableComponent {
  @Input({ required: true }) filters: QueueFilter[] = [];
  @Input({ required: true }) activeFilter: QueueFilter = 'All requests';
  @Input({ required: true }) isLoading = false;
  @Input({ required: true }) filteredTickets: Task[] = [];
  @Input() selectedTaskId: number | null = null;
  @Input({ required: true }) getUserName!: (userId: number | null) => string;
  @Input({ required: true }) getTeamName!: (teamId: number | null) => string;
  @Input({ required: true }) statusLabel!: (status: TaskStatus | null) => string;
  @Input({ required: true }) statusClass!: (status: TaskStatus | null) => string;
  @Input({ required: true }) priorityClass!: (priority: TaskPriority | null) => string;
  @Input({ required: true }) formatDateTime!: (value: string | null) => string;
  @Input({ required: true }) formatDueDate!: (value: string | null) => string;
  @Input({ required: true }) activeSortField: TaskSortField = 'updated';
  @Input({ required: true }) activeSortDirection: SortDirection = 'desc';

  @Output() filterChange = new EventEmitter<QueueFilter>();
  @Output() ticketSelect = new EventEmitter<number>();
  @Output() refresh = new EventEmitter<void>();
  @Output() createRequest = new EventEmitter<void>();
  @Output() sortChange = new EventEmitter<TaskSortField>();

  protected toggleSort(field: TaskSortField): void {
    this.sortChange.emit(field);
  }

  protected sortIndicator(field: TaskSortField): string {
    if (this.activeSortField !== field) {
      return '';
    }

    return this.activeSortDirection === 'asc' ? '^' : 'v';
  }
}
