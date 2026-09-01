import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresInSeconds: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'mcp_access_token';
  private readonly refreshKey = 'mcp_refresh_token';
  readonly isAuthenticated = signal(false);

  constructor(private http: HttpClient, private router: Router) {
    this.isAuthenticated.set(!!localStorage.getItem(this.tokenKey));
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  login(email: string, password: string) {
    return this.http
      .post<TokenResponse>(`${environment.apiBaseUrl}/auth/login`, { email, password })
      .pipe(tap((tokens) => this.storeTokens(tokens)));
  }

  bootstrap(orgSlug: string, orgName: string, email: string, password: string) {
    return this.http.post(`${environment.apiBaseUrl}/organizations/bootstrap`, {
      orgSlug,
      orgName,
      email,
      password,
    });
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshKey);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  private storeTokens(tokens: TokenResponse): void {
    localStorage.setItem(this.tokenKey, tokens.accessToken);
    localStorage.setItem(this.refreshKey, tokens.refreshToken);
    this.isAuthenticated.set(true);
  }
}
