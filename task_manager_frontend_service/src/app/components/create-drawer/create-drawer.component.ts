import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TaskPriority, TaskStatus, Team, User } from '../../core/business-api.types';
import { CreateTaskFormModel } from '../../shared/dashboard.types';

@Component({
  selector: 'app-create-drawer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-drawer.component.html',
  styleUrl: './create-drawer.component.scss'
})
export class CreateDrawerComponent {
  @Input({ required: true }) isOpen = false;
  @Input({ required: true }) form!: CreateTaskFormModel;
  @Input({ required: true }) users: User[] = [];
  @Input({ required: true }) teams: Team[] = [];
  @Input({ required: true }) statuses: TaskStatus[] = [];
  @Input({ required: true }) priorities: TaskPriority[] = [];
  @Input({ required: true }) isSaving = false;
  @Input({ required: true }) statusLabel!: (status: TaskStatus | null) => string;

  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<void>();
}
