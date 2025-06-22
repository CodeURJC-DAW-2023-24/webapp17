import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { User } from '../models/user';

export interface LoginRequest {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'https://localhost:8443/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const storedUser = sessionStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
    this.refreshUser().subscribe({
      error: () => this.logoutLocal()
    });
  }

  private saveUser(user: User | null) {
    if (user) {
      sessionStorage.setItem('currentUser', JSON.stringify(user));
    } else {
      sessionStorage.removeItem('currentUser');
    }
    this.currentUserSubject.next(user);
  }

  login(email: string, password: string): Observable<User> {
    const params = new HttpParams().set('email', email).set('password', password);
    return this.http.post<User>(this.apiUrl + "/login", null, { params, withCredentials: true }).pipe(
      tap(user => this.saveUser(user))
    );
  }

  logout(): Observable<string> {
  return this.http.post(this.apiUrl + "/logout", {}, { withCredentials: true, responseType: 'text' }).pipe(
    tap(() => this.logoutLocal())
  );
}


  private logoutLocal() {
    this.saveUser(null);
  }

  getCurrentUserFromServer(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`, { withCredentials: true }).pipe(
      tap(user => this.saveUser(user)),
      catchError(error => {
        if (error.status === 401) {
          this.logoutLocal();
        }
        return throwError(() => error);
      })
    );
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.getCurrentUser() !== null;
  }

  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'ADMIN';
  }

  isUser(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'USER';
  }

  getCurrentUserId(): number | null {
    return this.getCurrentUser()?.id || null;
  }

  getCurrentUserEmail(): string | null {
    return this.getCurrentUser()?.email || null;
  }

  getCurrentUsername(): string | null {
    return this.getCurrentUser()?.username || null;
  }

  refreshUser(): Observable<User> {
    return this.getCurrentUserFromServer();
  }
}
