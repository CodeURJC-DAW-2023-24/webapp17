import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interfaces to match your API DTOs
export interface IssueDTO {
  name: string;
  email: string;
  content: string;
}

export interface Issue {
  id?: number;
  name: string;
  email: string;
  content: string;
  date?: string; // LocalDateTime comes as string from API
}

@Injectable({
  providedIn: 'root'
})
export class IssueService {
  private apiUrl = 'http://localhost:8443/api/v1/issues';

  constructor(private http: HttpClient) {}

  /**
   * Create a new issue
   * @param issueData The issue data to create
   * @returns Observable with creation confirmation message
   */
  createIssue(issueData: IssueDTO): Observable<string> {
    return this.http.post(`${this.apiUrl}`, issueData, {
      responseType: 'text' // Since API returns plain text confirmation
    });
  }

  /**
   * Create issue with individual parameters
   * @param name Reporter's name
   * @param email Reporter's email
   * @param content Issue description/content
   * @returns Observable with creation confirmation message
   */
  createIssueWithParams(name: string, email: string, content: string): Observable<string> {
    const issueDto: IssueDTO = { name, email, content };
    return this.createIssue(issueDto);
  }

  /**
   * Get all issues (Admin only)
   * @returns Observable with array of issues
   */
  getAllIssues(): Observable<Issue[]> {
    return this.http.get<Issue[]>(`${this.apiUrl}/issues`);
  }

  /**
   * Delete an issue by ID (Admin only)
   * @param issueId The ID of the issue to delete
   * @returns Observable with deletion confirmation message
   */
  deleteIssue(issueId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${issueId}`, {
      responseType: 'text' // Since API returns plain text confirmation
    });
  }

  /**
   * Helper method to create an IssueDTO
   * @param name Reporter's name
   * @param email Reporter's email
   * @param content Issue description
   * @returns IssueDTO object
   */
  createIssueDto(name: string, email: string, content: string): IssueDTO {
    return { name, email, content };
  }

  /**
   * Helper method to validate email format
   * @param email Email to validate
   * @returns Boolean indicating if email is valid
   */
  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Helper method to validate issue data before submission
   * @param issueData The issue data to validate
   * @returns Object with validation result and error messages
   */
  validateIssueData(issueData: IssueDTO): { isValid: boolean; errors: string[] } {
    const errors: string[] = [];

    if (!issueData.name || issueData.name.trim().length === 0) {
      errors.push('Name is required');
    }

    if (!issueData.email || issueData.email.trim().length === 0) {
      errors.push('Email is required');
    } else if (!this.isValidEmail(issueData.email)) {
      errors.push('Please enter a valid email address');
    }

    if (!issueData.content || issueData.content.trim().length === 0) {
      errors.push('Issue content/description is required');
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }
}