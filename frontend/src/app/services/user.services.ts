import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interfaces for DTOs
export interface UserInfoDTO {
  id: number;
  username: string;
  email: string;
  postsCount: number;
  commentsCount: number;
}

export interface CreateUserRequestDTO {
  username: string;
  email: string;
  password: string;
  role: string; // 'ADMIN' or 'USER'
}

export interface ApiResponse {
  message?: string;
  error?: string;
  redirect?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  private readonly baseUrl = 'http://localhost:8080/api/v1'; // Adjust the URL according to your configuration
  
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    }),
    withCredentials: true // Important to maintain session
  };

  constructor(private http: HttpClient) { }

  /**
   * Gets all users (admin only)
   * @returns Observable<UserInfoDTO[]> List of users or error message
   */
  getAllUsers(): Observable<UserInfoDTO[] | string> {
    return this.http.get<UserInfoDTO[] | string>(`${this.baseUrl}/users`, this.httpOptions);
  }

  /**
   * Creates a new user (admin only)
   * @param userData User data to create
   * @returns Observable<string> Confirmation message
   */
  createUser(userData: CreateUserRequestDTO): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/user`, userData, this.httpOptions);
  }

  /**
   * Deletes a user by ID (admin only)
   * @param userId ID of the user to delete
   * @returns Observable<ApiResponse> API response
   */
  deleteUser(userId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${this.baseUrl}/users/${userId}`, this.httpOptions);
  }

  // Helper methods for error handling and validation

  /**
   * Checks if the error is due to insufficient permissions
   * @param error Error received from API
   * @returns boolean true if it's a permission error
   */
  isPermissionError(error: any): boolean {
    return error.status === 403;
  }

  /**
   * Checks if the error is a validation error
   * @param error Error received from API  
   * @returns boolean true if it's a validation error
   */
  isValidationError(error: any): boolean {
    return error.status === 400;
  }

  /**
   * Extracts error message from response
   * @param error Error received from API
   * @returns string Error message
   */
  getErrorMessage(error: any): string {
    if (error.error && typeof error.error === 'object' && error.error.error) {
      return error.error.error;
    }
    if (error.error && typeof error.error === 'string') {
      return error.error;
    }
    return 'An unknown error occurred';
  }

  /**
   * Gets redirect URL from error response
   * @param error Error received from API
   * @returns string | null Redirect URL if present
   */
  getRedirectUrl(error: any): string | null {
    if (error.error && typeof error.error === 'object' && error.error.redirect) {
      return error.error.redirect;
    }
    return null;
  }
}