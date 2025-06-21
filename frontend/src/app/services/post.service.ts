import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post, PostCreationDTO, PostPage } from '../models/post';




@Injectable({
  providedIn: 'root'
})
export class PostService {
  private readonly API_URL = '/api/v1/posts';

  constructor(private http: HttpClient) { }



  getPosts(page = 0, size = 3): Observable<PostPage> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PostPage>(this.API_URL, { params });
  }

  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.API_URL}/${id}`);
  }

  getMyPosts(page = 0, size = 3): Observable<PostPage> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PostPage>(`${this.API_URL}/me`, { params });
  }


  getPostsByUserId(userId: number, page = 0, size = 10): Observable<Post[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Post[]>(`${this.API_URL}/user/${userId}`, { params });
  }

  createPost(formData: FormData): Observable<any> {
    return this.http.post(this.API_URL + '/posts', formData);
  }


  updatePost(id: number, formData: FormData): Observable<any> {

    return this.http.put(`${this.API_URL}/${id}`, formData);
  }

  deletePost(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`);
  }
}
