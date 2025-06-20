// services/chat.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Updated interfaces to match your API DTOs
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

export interface AIPostResponse {
  // The API returns a string message, but you might want to extend this
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8443/api/v1/chatbot'; // Updated to match your port

  constructor(private http: HttpClient) {}

  /**
   * Send message history to chatbot and get AI response
   * @param history Array of messages with role and content
   * @returns Observable with AI response
   */
  sendMessage(history: Message[]): Observable<ChatResponse> {
    const chatRequest: ChatRequest = { history };
    return this.http.post<ChatResponse>(`${this.apiUrl}/send`, chatRequest);
  }

  /**
   * Generate an AI blog post and save it to the database
   * Only available for ADMIN users
   * @param tag The topic tag for the AI-generated post
   * @returns Observable with creation response
   */
  generateAIPost(tag: string): Observable<string> {
    const messageDTO: SimpleMessageDTO = { message: tag };
    return this.http.post(`${this.apiUrl}/ai-post`, messageDTO, { 
      responseType: 'text' // Since the API returns a string response
    });
  }

  /**
   * Helper method to create a message object
   * @param role The role (e.g., 'user', 'assistant', 'system')
   * @param content The message content
   * @returns Message object
   */
  createMessage(role: string, content: string): Message {
    return { role, content };
  }

  /**
   * Helper method to add a user message to history
   * @param history Current message history
   * @param content User message content
   * @returns Updated history with new user message
   */
  addUserMessage(history: Message[], content: string): Message[] {
    return [...history, this.createMessage('user', content)];
  }

  /**
   * Helper method to add assistant response to history
   * @param history Current message history
   * @param content Assistant response content
   * @returns Updated history with new assistant message
   */
  addAssistantMessage(history: Message[], content: string): Message[] {
    return [...history, this.createMessage('assistant', content)];
  }
}