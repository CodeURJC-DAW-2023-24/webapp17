import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../models/post';

// Interfaces de los DTOs
export interface PostDTO {
  title: string;
  content: string;
  tag: string;
  image: File | undefined; 
}



@Injectable({
  providedIn: 'root'
})
export class PostService {
  private readonly API_URL = '/api/v1/posts';

  constructor(private http: HttpClient) {}

  // Obtener todos los posts paginados
  getPosts(page = 0, size = 10): Observable<Post[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Post[]>(this.API_URL, { params });
  }

  // Obtener un post por ID
  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.API_URL}/${id}`);
  }

  // Obtener los posts del usuario logueado
  getMyPosts(page = 0, size = 10): Observable<Post[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Post[]>(`${this.API_URL}/me`, { params });
  }

  // Obtener los posts de un usuario por su ID
  getPostsByUserId(userId: number, page = 0, size = 10): Observable<Post[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Post[]>(`${this.API_URL}/user/${userId}`, { params });
  }

  // Crear un nuevo post
  createPost(dto: PostDTO): Observable<any> {
  const formData = new FormData();
  formData.append('title', dto.title);
  formData.append('content', dto.content);
  formData.append('tag', dto.tag);

  if (dto.image) {
    formData.append('image', dto.image, dto.image.name);
  }

  return this.http.post(this.API_URL + '/posts', formData);
}

  // Actualizar un post
  updatePost(id: number, dto: PostDTO): Observable<any> {
    const formData = new FormData();
    formData.append('title', dto.title);
    formData.append('content', dto.content);
    formData.append('tag', dto.tag);

    return this.http.put(`${this.API_URL}/${id}`, formData);
  }

  // Eliminar un post
  deletePost(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`);
  }
}
