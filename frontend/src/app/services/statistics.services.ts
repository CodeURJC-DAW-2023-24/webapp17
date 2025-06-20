import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interfaces for DTOs
export interface UserPostCountDTO {
  user: string;
  postCount: number;
}

export interface PostCommentCountDTO {
  title: string;
  commentCount: number;
}

export interface TagCountDTO {
  tag: string;
  count: number;
}

export interface ErrorResponseDTO {
  error: string;
  redirect: string;
}

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/statistics'; // Adjust the URL according to your configuration
  
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    }),
    withCredentials: true // Important to maintain session
  };

  constructor(private http: HttpClient) { }

  /**
   * Gets users with most posts (admin only)
   * @returns Observable<UserPostCountDTO[] | ErrorResponseDTO> List of users sorted by post count or error
   */
  getUsersWithMostPosts(): Observable<UserPostCountDTO[] | ErrorResponseDTO> {
    return this.http.get<UserPostCountDTO[] | ErrorResponseDTO>(`${this.baseUrl}/users`, this.httpOptions);
  }

  /**
   * Gets posts with most comments (admin only)
   * @returns Observable<PostCommentCountDTO[] | ErrorResponseDTO> List of posts sorted by comment count or error
   */
  getPostsWithMostComments(): Observable<PostCommentCountDTO[] | ErrorResponseDTO> {
    return this.http.get<PostCommentCountDTO[] | ErrorResponseDTO>(`${this.baseUrl}/posts`, this.httpOptions);
  }

  /**
   * Gets tags with most posts (admin only)
   * @returns Observable<TagCountDTO[] | ErrorResponseDTO> List of tags sorted by post count or error
   */
  getTagsWithMostPosts(): Observable<TagCountDTO[] | ErrorResponseDTO> {
    return this.http.get<TagCountDTO[] | ErrorResponseDTO>(`${this.baseUrl}/tags`, this.httpOptions);
  }

  /**
   * Gets all statistics data in a single call (admin only)
   * @returns Observable with all statistics combined
   */
  getAllStatistics(): Observable<{
    users: UserPostCountDTO[] | ErrorResponseDTO,
    posts: PostCommentCountDTO[] | ErrorResponseDTO,
    tags: TagCountDTO[] | ErrorResponseDTO
  }> {
    return new Observable(observer => {
      const results: {
        users: UserPostCountDTO[] | ErrorResponseDTO,
        posts: PostCommentCountDTO[] | ErrorResponseDTO,
        tags: TagCountDTO[] | ErrorResponseDTO
      } = {
        users: { error: 'Loading...', redirect: '' },
        posts: { error: 'Loading...', redirect: '' },
        tags: { error: 'Loading...', redirect: '' }
      };

      let completedRequests = 0;
      const totalRequests = 3;

      const checkCompletion = () => {
        if (completedRequests === totalRequests) {
          observer.next(results);
          observer.complete();
        }
      };

      // Get users statistics
      this.getUsersWithMostPosts().subscribe({
        next: (data) => {
          results.users = data;
          completedRequests++;
          checkCompletion();
        },
        error: (error) => {
          results.users = { error: this.getErrorMessage(error), redirect: this.getRedirectUrl(error) || '' };
          completedRequests++;
          checkCompletion();
        }
      });

      // Get posts statistics
      this.getPostsWithMostComments().subscribe({
        next: (data) => {
          results.posts = data;
          completedRequests++;
          checkCompletion();
        },
        error: (error) => {
          results.posts = { error: this.getErrorMessage(error), redirect: this.getRedirectUrl(error) || '' };
          completedRequests++;
          checkCompletion();
        }
      });

      // Get tags statistics
      this.getTagsWithMostPosts().subscribe({
        next: (data) => {
          results.tags = data;
          completedRequests++;
          checkCompletion();
        },
        error: (error) => {
          results.tags = { error: this.getErrorMessage(error), redirect: this.getRedirectUrl(error) || '' };
          completedRequests++;
          checkCompletion();
        }
      });
    });
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
   * Checks if the response is an error response
   * @param response Response from API
   * @returns boolean true if it's an error response
   */
  isErrorResponse(response: any): response is ErrorResponseDTO {
    return response && typeof response === 'object' && 'error' in response && 'redirect' in response;
  }

  /**
   * Type guard to check if response is UserPostCountDTO array
   * @param response Response from API
   * @returns boolean true if it's a UserPostCountDTO array
   */
  isUserPostCountArray(response: any): response is UserPostCountDTO[] {
    return Array.isArray(response) && (response.length === 0 || ('user' in response[0] && 'postCount' in response[0]));
  }

  /**
   * Type guard to check if response is PostCommentCountDTO array
   * @param response Response from API
   * @returns boolean true if it's a PostCommentCountDTO array
   */
  isPostCommentCountArray(response: any): response is PostCommentCountDTO[] {
    return Array.isArray(response) && (response.length === 0 || ('title' in response[0] && 'commentCount' in response[0]));
  }

  /**
   * Type guard to check if response is TagCountDTO array
   * @param response Response from API
   * @returns boolean true if it's a TagCountDTO array
   */
  isTagCountArray(response: any): response is TagCountDTO[] {
    return Array.isArray(response) && (response.length === 0 || ('tag' in response[0] && 'count' in response[0]));
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

  /**
   * Sorts users by post count in descending order
   * @param users Array of UserPostCountDTO
   * @returns UserPostCountDTO[] Sorted array
   */
  sortUsersByPostCount(users: UserPostCountDTO[]): UserPostCountDTO[] {
    return [...users].sort((a, b) => b.postCount - a.postCount);
  }

  /**
   * Sorts posts by comment count in descending order
   * @param posts Array of PostCommentCountDTO
   * @returns PostCommentCountDTO[] Sorted array
   */
  sortPostsByCommentCount(posts: PostCommentCountDTO[]): PostCommentCountDTO[] {
    return [...posts].sort((a, b) => b.commentCount - a.commentCount);
  }

  /**
   * Sorts tags by count in descending order
   * @param tags Array of TagCountDTO
   * @returns TagCountDTO[] Sorted array
   */
  sortTagsByCount(tags: TagCountDTO[]): TagCountDTO[] {
    return [...tags].sort((a, b) => b.count - a.count);
  }
}