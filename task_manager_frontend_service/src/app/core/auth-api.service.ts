import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { AUTH_API_BASE_URL } from './api.config';
import { LoginRequestPayload, LoginResponse, RegisterRequestPayload } from './auth-api.types';

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);

  login(payload: LoginRequestPayload) {
    return this.http.post<LoginResponse>(`${AUTH_API_BASE_URL}/auth/login`, payload);
  }

  register(payload: RegisterRequestPayload) {
    return this.http.post(`${AUTH_API_BASE_URL}/auth/register`, payload, {
      responseType: 'text'
    });
  }
}
