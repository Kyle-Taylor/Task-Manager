import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TaskComment, TaskPriority, TaskStatus } from '../../core/business-api.types';
import { RequestModalContext } from '../../shared/dashboard.types';

@Component({
  selector: 'app-request-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './request-modal.component.html',
  styleUrl: './request-modal.component.scss'
})
export class RequestModalComponent {
  @Input({ required: true }) context: RequestModalContext | null = null;
  @Input({ required: true }) statusLabel!: (status: TaskStatus | null) => string;
  @Input({ required: true }) statusClass!: (status: TaskStatus | null) => string;
  @Input({ required: true }) formatDateTime!: (value: string | null) => string;
  @Input({ required: true }) formatDueDate!: (value: string | null) => string;

  @Output() close = new EventEmitter<void>();
  @Output() updateStatus = new EventEmitter<TaskStatus>();
  @Output() detailTitleChange = new EventEmitter<string>();
  @Output() detailDescriptionChange = new EventEmitter<string>();
  @Output() detailPriorityChange = new EventEmitter<TaskPriority>();
  @Output() saveDetails = new EventEmitter<void>();
  @Output() assignmentUserIdChange = new EventEmitter<number | null>();
  @Output() assignmentTeamIdChange = new EventEmitter<number | null>();
  @Output() saveAssignment = new EventEmitter<void>();
  @Output() beginCommentEdit = new EventEmitter<TaskComment>();
  @Output() editingCommentTextChange = new EventEmitter<string>();
  @Output() newCommentTextChange = new EventEmitter<string>();
  @Output() createComment = new EventEmitter<void>();
  @Output() saveCommentEdit = new EventEmitter<number>();
  @Output() cancelCommentEdit = new EventEmitter<void>();
  @Output() deleteComment = new EventEmitter<number>();
}
