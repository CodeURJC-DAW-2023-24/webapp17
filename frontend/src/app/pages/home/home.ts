import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { ChatService, Message } from '../../services/chat.service';
import { CommentService } from '../../services/comment.service';
import { Post } from '../../models/post';

@Component({
  selector: 'app-home',
  imports: [], standalone: true,
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit, AfterViewChecked {
  @ViewChild('chatBox') chatBox!: ElementRef;

  // Pagination properties
  posts: Post[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  hasPrevious: boolean = false;
  hasNext: boolean = false;
  previousPage: number = 0;
  nextPage: number = 0;

  // User and authentication
  currentUser: User | null = null;
  isAdmin: boolean = false;

  // Form data for new post
  newPost = {
    title: '',
    content: '',
    tag: '',
    imageName: '',
    image: null as File | null
  };

  // AI post generation
  aiPostTag: string = '';

  // Comments functionality
  commentText: string = '';
  visibleComments: Set<number> = new Set();

  // Chat functionality
  chatMessages: Message[] = [];
  chatMessage: string = '';
  private shouldScrollToBottom = false;

  constructor(
    private postService: PostService,
    private authService: AuthService,
    private chatService: ChatService,
    private route: ActivatedRoute,
    private router: Router,
    private commentService: CommentService
  ) {}

  ngOnInit(): void {
    // Initialize user data
    this.loadUserData();
    
    // Load posts with pagination from query params
    this.route.queryParams.subscribe(params => {
      this.currentPage = +(params['page'] || 0);
      this.pageSize = +(params['size'] || 10);
      this.loadPosts();
    });
  }

  ngAfterViewChecked(): void {
    if (this.shouldScrollToBottom) {
      this.scrollChatToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  // User and authentication methods
  private loadUserData(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.isAdmin = this.authService.isAdmin();
  }

  // Posts loading and pagination
  private loadPosts(): void {
    this.postService.getPosts(this.currentPage, this.pageSize).subscribe({
      next: (response: { content: Post[]; totalPages: number; first: any; last: any; }) => {
        this.posts = response.content;
        this.totalPages = response.totalPages;
        this.hasPrevious = !response.first;
        this.hasNext = !response.last;
        this.previousPage = this.currentPage - 1;
        this.nextPage = this.currentPage + 1;
      },
      error: (error: any) => {
        console.error('Error loading posts:', error);
      }
    });
  }

  // Comments functionality
  toggleComments(postId: number): void {
    if (this.visibleComments.has(postId)) {
      this.visibleComments.delete(postId);
    } else {
      this.visibleComments.add(postId);
    }
  }

  isCommentsVisible(postId: number): boolean {
    return this.visibleComments.has(postId);
  }

  addComment(postId: number, commentText: string): void {
    this.commentService.addComment(postId, commentText).subscribe()
  }

  deleteComment(commentId: number): void {
  this.commentService.deleteComment(commentId).subscribe()
  }

  // Post management
  createPost(): void {
    if (!this.newPost.title.trim() || !this.newPost.content.trim()) {
      alert('Por favor, completa los campos obligatorios');
      return;
    }

    const formData = new FormData();
    formData.append('title', this.newPost.title);
    formData.append('content', this.newPost.content);
    formData.append('tag', this.newPost.tag);
    formData.append('imageName', this.newPost.imageName);
    
    if (this.newPost.image) {
      formData.append('image', this.newPost.image);
    }

    this.postService.createPost(formData).subscribe({
      next: (post: any) => {
        // Add new post to the beginning of the list
        this.posts.unshift(post);
        // Reset form
        this.resetNewPostForm();
        alert('Post creado exitosamente');
      },
      error: (error: any) => {
        console.error('Error creating post:', error);
        alert('Error al crear el post');
      }
    });
  }

  deletePost(postId: number): void {
    if (confirm('¿Estás seguro de que quieres eliminar este post?')) {
      this.postService.deletePost(postId).subscribe({
        next: () => {
          // Remove post from the list
          this.posts = this.posts.filter(p => p.id !== postId);
        },
        error: (error: any) => {
          console.error('Error deleting post:', error);
          alert('Error al eliminar el post');
        }
      });
    }
  }

  generateAIPost(): void {
    if (!this.aiPostTag.trim()) {
      alert('Por favor, ingresa una temática');
      return;
    }

    this.chatService.generateAIPost(this.aiPostTag).subscribe({
      next: (post: any) => {
        // Add AI generated post to the beginning of the list
        this.posts.unshift(post);
        this.aiPostTag = ''; // Reset form
        alert('Post generado exitosamente por IA');
      },
      error: (error: any) => {
        console.error('Error generating AI post:', error);
        alert('Error al generar el post con IA');
      }
    });
  }

  // File handling
  onImageSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Validate file type
      if (!file.type.match(/image\/(jpeg|jpg)/)) {
        alert('Por favor, selecciona un archivo JPG o JPEG');
        event.target.value = '';
        return;
      }
      
      // Validate file size (e.g., max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('El archivo es demasiado grande. Máximo 5MB');
        event.target.value = '';
        return;
      }
      
      this.newPost.image = file;
    }
  }

  // Chat functionality
  sendChatMessage(): void {
    if (!this.chatMessage.trim()) {
      return;
    }

    // Add user message to chat
    const userMessage: Message = {
      role: 'user',
      content: this.chatMessage
    };
    this.chatMessages.push(userMessage);
    
    // Prepare message for sending
    const messageToSend = this.chatMessage;
    this.chatMessage = ''; // Clear input immediately
    this.shouldScrollToBottom = true;

    // Send message to backend
    this.chatService.sendChat(this.chatMessages).subscribe({
      next: (response: { response: any; }) => {
        // Add bot response to chat
        const botMessage: Message = {
          role: 'assistant',
          content: response.response
        };
        this.chatMessages.push(botMessage);
        this.shouldScrollToBottom = true;
      },
      error: (error: any) => {
        console.error('Error sending chat message:', error);
        // Add error message to chat
        const errorMessage: Message = {
          role: 'assistant',
          content: 'Lo siento, hubo un error al procesar tu mensaje.'
        };
        this.chatMessages.push(errorMessage);
        this.shouldScrollToBottom = true;
      }
    });
  }

  private scrollChatToBottom(): void {
    try {
      if (this.chatBox) {
        this.chatBox.nativeElement.scrollTop = this.chatBox.nativeElement.scrollHeight;
      }
    } catch (err) {
      console.error('Error scrolling chat to bottom:', err);
    }
  }

  // Helper methods
  private resetNewPostForm(): void {
    this.newPost = {
      title: '',
      content: '',
      tag: '',
      imageName: '',
      image: null
    };
  }
}
export interface User {
  id: number;
  role: 'ADMIN' | 'USER';
  username: string;
  password?: string; // opcional, no se suele enviar al front
  email: string;
  posts?: Post[];
  comments?: Comment[];
}

