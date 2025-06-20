import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Updated interfaces to match your API DTOs
export interface PostResponseDTO {
  id: number;
  title: string;
  content: string;
  tag: string;
  date: string; // LocalDateTime comes as string from API
  image: string;
  userId: number;
  username: string;
  totalComments: number;
}

export interface PostDTO {
  title: string;
  content: string;
  tag: string;
}

export interface CommentDTO {
  text: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    pageSize: number;
    pageNumber: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8443/api/v1/posts'; // Updated to match your controller

  constructor(private http: HttpClient) { }

  // Get paginated posts
  getPosts(page: number = 0, size: number = 10): Observable<PageResponse<PostResponseDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<PostResponseDTO>>(this.apiUrl, { params });
  }

  // Get single post by ID
  getPost(id: number): Observable<PostResponseDTO> {
    return this.http.get<PostResponseDTO>(`${this.apiUrl}/${id}`);
  }

  // Create new post using FormData (to match @ModelAttribute)
  createPost( formData: FormData): Observable<PostDTO> {
    return this.http.post<PostDTO>(`${this.apiUrl}`, formData);
  }

  // Update post
  updatePost(id: number, postData: PostDTO): Observable<PostDTO> {
    const formData = new FormData();
    formData.append('title', postData.title);
    formData.append('content', postData.content);
    formData.append('tag', postData.tag);

    return this.http.put<PostDTO>(`${this.apiUrl}/${id}`, formData);
  }
  updatePost2(id: number, formData: FormData): Observable<PostDTO> {
    return this.http.put<PostDTO>(`${this.apiUrl}/${id}`, formData);
  }

  // Delete post
  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Get posts of the currently authenticated user
  getMyPosts(page: number = 0, size: number = 10): Observable<PageResponse<PostResponseDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<PostResponseDTO>>(`${this.apiUrl}/me`, { params });
  }

  // Get posts by specific user ID
  getPostsByUserId(userId: number, page: number = 0, size: number = 10): Observable<PageResponse<PostResponseDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<PostResponseDTO>>(`${this.apiUrl}/user/${userId}`, { params });
  }
}
