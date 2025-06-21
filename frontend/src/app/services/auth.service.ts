import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { User } from '../models/user';

// Updated interfaces to match your API DTOs


export interface LoginRequest {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8443/api/auth'; // Updated to match your controller path
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  /**
   * Login with email and password using form parameters
   * @param email User email
   * @param password User password
   * @returns Observable with login response
   */
  login(email: string, password: string): Observable<User> {
    const params = new HttpParams().set('email', email).set('password', password);

    return this.http.post<User>(this.apiUrl + "/login", null, { params }).pipe(
      tap(user => {
        // Guardamos usuario en BehaviorSubject y localStorage
        this.currentUserSubject.next(user);
        localStorage.setItem('currentUser', JSON.stringify(user));
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(this.apiUrl+ "/logout", {}).pipe(
      tap(() => {
        this.currentUserSubject.next(null);
        localStorage.removeItem('currentUser');
      })
    );
  }



  /**
   * Get current authenticated user from server
   * @returns Observable with current user data
   */
  getCurrentUserFromServer(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`).pipe(
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

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  };



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
    return this.getCurrentUser()?.username || null;
  }

  /**
   * Refresh current user data from server
   * @returns Observable with updated user data
   */
  refreshUser(): Observable<User> {
    return this.getCurrentUserFromServer();
  }
}