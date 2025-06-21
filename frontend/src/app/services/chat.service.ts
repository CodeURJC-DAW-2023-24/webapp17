import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Message {
  role: string;
  content: string;
}

export interface ChatRequest {
  history: Message[];
}

export interface ChatResponse {
  response: string;
}

export interface SimpleMessageDTO {
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private chatApiUrl = 'https://localhost:8443/api/v1/chatbot/send';
  private generatePostApiUrl = '/api/v1/chatbot/ai-post';

  constructor(private http: HttpClient) {}

  
  sendChat(history: Message[]): Observable<ChatResponse> {
    const body: ChatRequest = { history };
    return this.http.post<ChatResponse>(this.chatApiUrl, body);
  }

  /** Genera un post AI con el tag indicado (solo admin) */
  generateAIPost(tag: string): Observable<string> {
    const body: SimpleMessageDTO = { message: tag };
    return this.http.post(this.generatePostApiUrl, body, { responseType: 'text' });
  }

}
