import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Team } from '../../core/business-api.types';
import { CreateUserFormModel } from '../../shared/dashboard.types';

@Component({
  selector: 'app-login-screen',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login-screen.component.html',
  styleUrl: './login-screen.component.scss'
})
export class LoginScreenComponent {
  @Input({ required: true }) isLoading = false;
  @Input({ required: true }) email = '';
  @Input({ required: true }) password = '';
  @Input({ required: true }) errorMessage = '';
  @Input({ required: true }) teams: Team[] = [];
  @Input({ required: true }) isCreateMode = false;
  @Input({ required: true }) createUserForm!: CreateUserFormModel;
  @Input({ required: true }) createErrorMessage = '';

  @Output() emailChange = new EventEmitter<string>();
  @Output() passwordChange = new EventEmitter<string>();
  @Output() signIn = new EventEmitter<void>();
  @Output() toggleCreateMode = new EventEmitter<void>();
  @Output() createUsernameChange = new EventEmitter<string>();
  @Output() createEmailChange = new EventEmitter<string>();
  @Output() createPasswordChange = new EventEmitter<string>();
  @Output() createConfirmPasswordChange = new EventEmitter<string>();
  @Output() createTeamIdChange = new EventEmitter<number | null>();
  @Output() createAccount = new EventEmitter<void>();
}
