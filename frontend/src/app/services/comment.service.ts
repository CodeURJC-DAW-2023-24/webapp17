// services/comment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interfaces to match your API DTOs
export interface CommentDTO {
  text: string;
}

export interface CommentResponseDTO {
  Id: number;     // Note: Capital 'I' to match your backend DTO
  Text: string;   // Note: Capital 'T' to match your backend DTO
  UserId: number; // Note: Capital 'U' to match your backend DTO
}

// Extended interface for better type safety in frontend
export interface Comment extends CommentResponseDTO {
  id: number;     // Lowercase for consistency in frontend
  text: string;   // Lowercase for consistency in frontend
  userId: number; // Lowercase for consistency in frontend
  date?: string;  // Optional date field
  username?: string; // Optional username field
}

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = 'https://localhost:8443/api/v1/comments';

  constructor(private http: HttpClient) {}

  /**
   * Add a comment to a specific post
   * @param postId The ID of the post to comment on
   * @param commentText The text content of the comment
   * @returns Observable with void response (201 Created)
   */
  addComment(postId: number, commentText: string): Observable<void> {
    const commentDto: CommentDTO = { text: commentText };
    return this.http.post<void>(`${this.apiUrl}/post/${postId}`, commentDto);
  }

  /**
   * Get a comment by its ID
   * @param commentId The ID of the comment to retrieve
   * @returns Observable with comment data
   */
  getComment(commentId: number): Observable<CommentResponseDTO> {
    return this.http.get<CommentResponseDTO>(`${this.apiUrl}/${commentId}`);
  }

  /**
   * Update a comment by its ID
   * @param commentId The ID of the comment to update
   * @param commentText The new text content
   * @returns Observable with updated comment DTO
   */
  updateComment(commentId: number, commentText: string): Observable<CommentDTO> {
    const commentDto: CommentDTO = { text: commentText };
    return this.http.put<CommentDTO>(`${this.apiUrl}/${commentId}`, commentDto);
  }

  /**
   * Delete a comment by its ID
   * @param commentId The ID of the comment to delete
   * @returns Observable with void response (204 No Content)
   */
  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${commentId}`);
  }

  /**
   * Helper method to convert CommentResponseDTO to Comment interface
   * @param dto The DTO from the API
   * @returns Comment object with normalized property names
   */
  convertFromDto(dto: CommentResponseDTO): Comment {
    return {
      ...dto,
      id: dto.Id,
      text: dto.Text,
      userId: dto.UserId
    };
  }

  /**
   * Helper method to create a CommentDTO
   * @param text The comment text
   * @returns CommentDTO object
   */
  createCommentDto(text: string): CommentDTO {
    return { text };
  }
}