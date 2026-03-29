import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthenticationDTO, LoginResponseDTO, RegisterDTO, DecodedToken, UserRole } from '../../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient, private router: Router) {}

  login(data: AuthenticationDTO): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(`${this.apiUrl}/login`, data).pipe(
      tap(res => localStorage.setItem(this.TOKEN_KEY, res.token))
    );
  }

  register(data: RegisterDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/register`, data);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    const decoded = this.decodeToken(token);
    return decoded ? decoded.exp * 1000 > Date.now() : false;
  }

  getDecodedToken(): DecodedToken | null {
    const token = this.getToken();
    return token ? this.decodeToken(token) : null;
  }

  getRole(): UserRole | null {
    return this.getDecodedToken()?.role ?? null;
  }

  isManager(): boolean {
    const role = this.getRole();
    return role === 'MANAGER' || role === 'ADMIN';
  }

  private decodeToken(token: string): DecodedToken | null {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload)) as DecodedToken;
    } catch {
      return null;
    }
  }
}
