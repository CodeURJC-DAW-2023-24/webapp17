import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class StatisticsService {
  private baseUrl = 'https://localhost:8443/api/v1/statistics';

  constructor(private http: HttpClient) {}

  getUsersWithMostPosts() {
    return lastValueFrom(this.http.get<{ user: string; postCount: number }[]>(`${this.baseUrl}/users`));
  }

  getPostsWithMostComments() {
    return lastValueFrom(this.http.get<{ title: string; commentCount: number }[]>(`${this.baseUrl}/posts`));
  }

  getTagsWithMostPosts() {
    return lastValueFrom(this.http.get<{ tag: string; count: number }[]>(`${this.baseUrl}/tags`));
  }
}
