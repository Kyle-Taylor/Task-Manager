import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

import { Task, Team, User } from '../../core/business-api.types';
import { TeamLoadItem } from '../../shared/dashboard.types';

@Component({
  selector: 'app-reports-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports-analytics.component.html',
  styleUrl: './reports-analytics.component.scss'
})
export class ReportsAnalyticsComponent {
  @Input({ required: true }) tasks: Task[] = [];
  @Input({ required: true }) teams: Team[] = [];
  @Input({ required: true }) users: User[] = [];
  @Input({ required: true }) teamLoad: TeamLoadItem[] = [];
  @Input({ required: true }) formatDueDate!: (value: string | null) => string;

  protected get overdueTasks(): Task[] {
    return this.tasks
      .filter(task => task.dueDate && task.status !== 'COMPLETED' && task.status !== 'CANCELLED')
      .filter(task => new Date(task.dueDate as string).getTime() < Date.now())
      .sort((left, right) => new Date(left.dueDate as string).getTime() - new Date(right.dueDate as string).getTime())
      .slice(0, 6);
  }

  protected get busiestTeam(): string {
    return this.teamLoad[0]?.team.name ?? 'No team data';
  }

  protected get completedRate(): number {
    if (!this.tasks.length) {
      return 0;
    }
    return Math.round((this.tasks.filter(task => task.status === 'COMPLETED').length / this.tasks.length) * 100);
  }

  protected get unassignedCount(): number {
    return this.tasks.filter(task => task.assignedUserId === null).length;
  }

  protected get unreadCount(): number {
    return this.tasks.filter(task => task.readStatus === 'UNREAD').length;
  }

  protected get unreadAssignedCount(): number {
    return this.tasks.filter(task => task.assignedUserId !== null && task.readStatus === 'UNREAD').length;
  }

  protected get averageOpenAgeDays(): number {
    const openTasks = this.tasks.filter(task => task.status !== 'COMPLETED' && task.status !== 'CANCELLED');
    if (!openTasks.length) {
      return 0;
    }

    const totalDays = openTasks.reduce((sum, task) => {
      if (!task.createdAt) {
        return sum;
      }

      const ageMs = Date.now() - new Date(task.createdAt).getTime();
      return sum + Math.max(0, ageMs / (1000 * 60 * 60 * 24));
    }, 0);

    return Math.round((totalDays / openTasks.length) * 10) / 10;
  }

  protected get statusMix() {
    const statuses = [
      { label: 'Open ', key: 'OPEN' },
      { label: 'In progress ', key: 'IN_PROGRESS' },
      { label: 'Completed ', key: 'COMPLETED' },
      { label: 'Cancelled ', key: 'CANCELLED' }
    ] as const;

    const total = this.tasks.length || 1;

    return statuses.map(status => {
      const count = this.tasks.filter(task => task.status === status.key).length;
      return {
        ...status,
        count,
        width: Math.max(count ? 8 : 0, Math.round((count / total) * 100))
      };
    });
  }

  protected get agingBuckets() {
    const openTasks = this.tasks.filter(task => task.status !== 'COMPLETED' && task.status !== 'CANCELLED');
    const buckets = [
      { label: '0-1 days ', min: 0, max: 1 },
      { label: '2-3 days ', min: 1, max: 3 },
      { label: '4-7 days ', min: 3, max: 7 },
      { label: '7+ days ', min: 7, max: Number.POSITIVE_INFINITY }
    ];

    const total = openTasks.length || 1;

    return buckets.map(bucket => {
      const count = openTasks.filter(task => {
        if (!task.createdAt) {
          return false;
        }

        const ageDays = (Date.now() - new Date(task.createdAt).getTime()) / (1000 * 60 * 60 * 24);
        return ageDays > bucket.min && ageDays <= bucket.max;
      }).length;

      return {
        label: bucket.label,
        count,
        width: Math.max(count ? 10 : 0, Math.round((count / total) * 100))
      };
    });
  }

  protected get assigneeHotspots() {
    return this.users.map(user => {
      const assignedTasks = this.tasks.filter(task => task.assignedUserId === user.id && task.status !== 'COMPLETED' && task.status !== 'CANCELLED');
      const unreadTasks = assignedTasks.filter(task => task.readStatus === 'UNREAD').length;

      return {
        id: user.id,
        username: user.username,
        openCount: assignedTasks.length,
        unreadCount: unreadTasks
      };
    })
      .filter(user => user.openCount > 0 || user.unreadCount > 0)
      .sort((left, right) => {
        if (right.unreadCount !== left.unreadCount) {
          return right.unreadCount - left.unreadCount;
        }
        return right.openCount - left.openCount;
      })
      .slice(0, 5);
  }

  protected get unreadTasksByTeam() {
    return this.teams.map(team => {
      const unreadCount = this.tasks.filter(task => task.assignedTeamId === team.id && task.readStatus === 'UNREAD').length;
      return {
        id: team.id,
        name: team.name,
        unreadCount
      };
    })
      .filter(team => team.unreadCount > 0)
      .sort((left, right) => right.unreadCount - left.unreadCount)
      .slice(0, 5);
  }

  protected get analyticsCards() {
    return [
      {
        label: 'Busiest team',
        value: this.busiestTeam,
        detail: `${this.teamLoad[0]?.openCount ?? 0} open requests`
      },
      {
        label: 'Completion rate',
        value: `${this.completedRate}%`,
        detail: `${this.tasks.filter(task => task.status === 'COMPLETED').length} tickets resolved`
      },
      {
        label: 'Unassigned backlog',
        value: String(this.unassignedCount),
        detail: 'Requests waiting for ownership'
      },
      {
        label: 'Unread backlog',
        value: String(this.unreadCount),
        detail: `${this.unreadAssignedCount} assigned tasks still unread`
      },
      {
        label: 'Average open age',
        value: `${this.averageOpenAgeDays}d`,
        detail: 'Average age of unresolved requests'
      }
    ];
  }
}
