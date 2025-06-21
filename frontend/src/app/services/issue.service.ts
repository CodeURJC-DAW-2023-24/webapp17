import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Issue } from '../models/issue';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class IssueService {

  private baseUrl = 'http://localhost:8443/api/v1/issues';

  constructor(private http: HttpClient) {}


  createIssue(issue: Issue): Observable<any> {
    return this.http.post(this.baseUrl, issue);
  }

  // admin required
  deleteIssue(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  // admin  required
  getAllIssues(): Observable<Issue[]> {
    return this.http.get<Issue[]>(`${this.baseUrl}/issues`);
  }

 
}
