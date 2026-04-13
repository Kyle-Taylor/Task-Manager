import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Task, User } from '../../core/business-api.types';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.scss'
})
export class TopbarComponent {
  @Input({ required: true }) activeNavigation = 'Requests';
  @Input({ required: true }) unreadTasks: Task[] = [];
  @Input({ required: true }) theme: 'light' | 'dark' = 'light';
  @Input({ required: true }) currentUser: User | null = null;
  @Output() navigate = new EventEmitter<string>();
  @Output() openAlertTask = new EventEmitter<number>();
  @Output() themeChange = new EventEmitter<'light' | 'dark'>();
  @Output() signOut = new EventEmitter<void>();

  protected isAlertsOpen = false;
  protected isSettingsOpen = false;
  protected isUserMenuOpen = false;

  protected readonly topNavItems = [
    'Dashboard',
    'Requests',
    'Documents',
    'Reports',
    'Assets'
  ];

  protected toggleAlerts(): void {
    this.isAlertsOpen = !this.isAlertsOpen;
    if (this.isAlertsOpen) {
      this.isSettingsOpen = false;
      this.isUserMenuOpen = false;
    }
  }

  protected toggleSettings(): void {
    this.isSettingsOpen = !this.isSettingsOpen;
    if (this.isSettingsOpen) {
      this.isAlertsOpen = false;
      this.isUserMenuOpen = false;
    }
  }

  protected toggleUserMenu(): void {
    this.isUserMenuOpen = !this.isUserMenuOpen;
    if (this.isUserMenuOpen) {
      this.isAlertsOpen = false;
      this.isSettingsOpen = false;
    }
  }

  protected selectTheme(theme: 'light' | 'dark'): void {
    this.themeChange.emit(theme);
    this.isSettingsOpen = false;
  }

  protected selectAlertTask(taskId: number): void {
    this.openAlertTask.emit(taskId);
    this.isAlertsOpen = false;
  }

  protected userInitials(): string {
    const username = this.currentUser?.username ?? 'U';
    return username.slice(0, 2).toUpperCase();
  }
}
