export interface LoginRequestPayload {
  email: string;
  password: string;
}

export interface RegisterRequestPayload {
  profileUserId: number;
  email: string;
  password: string;
  role: string;
}

export interface LoginResponse {
  token: string;
  profileUserId: number;
  email: string;
  role: string;
  expiresIn: number;
}
