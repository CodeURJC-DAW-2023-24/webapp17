import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

// Updated interfaces to match your API DTOs
export interface UsrDto {
  id: number;
  email: string;
  nombre: string; // Note: maps to username in your backend
  role: 'USER' | 'ADMIN';
}

export interface LoginRequest {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8443/api/auth'; // Updated to match your controller path
  private currentUserSubject = new BehaviorSubject<UsrDto | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Check if user is authenticated on service initialization
    this.checkAuthStatus();
  }

  /**
   * Login with email and password using form parameters
   * @param email User email
   * @param password User password
   * @returns Observable with login response
   */
  login(email: string, password: string): Observable<string> {
    // Create form parameters as your controller expects @RequestParam
    const params = new HttpParams()
      .set('email', email)
      .set('password', password);

    return this.http.post(`${this.apiUrl}/login`, null, { 
      params,
      responseType: 'text' // Since API returns plain text response
    }).pipe(
      tap(() => {
        // After successful login, get user info
        this.getCurrentUserFromServer().subscribe();
      }),
      catchError(error => {
        console.error('Login failed:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Logout the current user
   * @returns Observable with logout response
   */
  logout(): Observable<string> {
    return this.http.post(`${this.apiUrl}/logout`, null, {
      responseType: 'text'
    }).pipe(
      tap(() => {
        // Clear current user from subject
        this.currentUserSubject.next(null);
      }),
      catchError(error => {
        console.error('Logout failed:', error);
        // Even if logout fails on server, clear local state
        this.currentUserSubject.next(null);
        return throwError(() => error);
      })
    );
  }

  /**
   * Get current authenticated user from server
   * @returns Observable with current user data
   */
  getCurrentUserFromServer(): Observable<UsrDto> {
    return this.http.get<UsrDto>(`${this.apiUrl}/me`).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
      }),
      catchError(error => {
        if (error.status === 401) {
          // User not authenticated
          this.currentUserSubject.next(null);
        }
        return throwError(() => error);
      })
    );
  }

  /**
   * Check authentication status on service initialization
   */
  private checkAuthStatus(): void {
    this.getCurrentUserFromServer().subscribe({
      next: () => {
        // User is authenticated, currentUserSubject updated in tap operator
      },
      error: () => {
        // User not authenticated or error occurred
        this.currentUserSubject.next(null);
      }
    });
  }

  /**
   * Get current user from local state
   * @returns Current user or null
   */
  getCurrentUser(): UsrDto | null {
    return this.currentUserSubject.value;
  }

  /**
   * Check if user is logged in
   * @returns Boolean indicating if user is logged in
   */
  isLoggedIn(): boolean {
    return this.getCurrentUser() !== null;
  }

  /**
   * Check if current user is admin
   * @returns Boolean indicating if user has admin role
   */
  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'ADMIN';
  }

  /**
   * Check if current user is regular user
   * @returns Boolean indicating if user has user role
   */
  isUser(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'USER';
  }

  /**
   * Get current user ID
   * @returns User ID or null
   */
  getCurrentUserId(): number | null {
    return this.getCurrentUser()?.id || null;
  }

  /**
   * Get current user email
   * @returns User email or null
   */
  getCurrentUserEmail(): string | null {
    return this.getCurrentUser()?.email || null;
  }

  /**
   * Get current username
   * @returns Username or null
   */
  getCurrentUsername(): string | null {
    return this.getCurrentUser()?.nombre || null;
  }

  /**
   * Refresh current user data from server
   * @returns Observable with updated user data
   */
  refreshUser(): Observable<UsrDto> {
    return this.getCurrentUserFromServer();
  }
}