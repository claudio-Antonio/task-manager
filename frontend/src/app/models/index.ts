export type UserRole = 'ADMIN' | 'MANAGER' | 'COLLABORATOR';
export type TaskStatus = 'COMPLETE' | 'NOT_COMPLETE';

export interface AuthenticationDTO {
  login: string;
  password: string;
}

export interface LoginResponseDTO {
  token: string;
}

export interface RegisterDTO {
  login: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface TaskRequestDTO {
  title: string;
  description: string;
  status: TaskStatus;
  userID: number;
}

export interface TaskResponseDTO {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
  userId: number;
}

export interface DecodedToken {
  sub: string;
  role: UserRole;
  exp: number;
}
