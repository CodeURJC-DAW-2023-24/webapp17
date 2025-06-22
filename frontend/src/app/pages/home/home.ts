import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { ChatService, Message } from '../../services/chat.service';
import { CommentService } from '../../services/comment.service';
import { Post } from '../../models/post';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { User } from '../../models/user';
import { CustomDatePipe } from '../../Pipes/custom-date.pipe';
import { Observable } from 'rxjs/internal/Observable';

@Component({
  selector: 'app-home',
  imports: [ReactiveFormsModule, CommonModule, FormsModule, RouterModule, CustomDatePipe], standalone: true,
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit, AfterViewChecked {
  @ViewChild('chatBox') chatBox!: ElementRef;

  posts: Post[] = [];
  currentPage = 0;
  totalPages = 0;
  size = 3;
  hasNext = false;
  hasPrevious = false;
  commentsVisible: { [postId: number]: boolean } = {};
  newComments: { [postId: number]: string } = {};
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
  currentUser$!: Observable<User | null>;


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
  ) { }

  ngOnInit(): void {
    // Initialize user data
    this.currentUser$ = this.authService.currentUser$;

    this.loadPosts(this.currentPage);

  }

  ngAfterViewChecked(): void {
    if (this.shouldScrollToBottom) {
      this.scrollChatToBottom();
      this.shouldScrollToBottom = false;
    }
  }



  // Posts loading and pagination
  loadPosts(page: number): void {
    this.postService.getPosts(page, this.size).subscribe(response => {
      this.posts = response.content;
      this.currentPage = page;
      this.totalPages = response.totalPages;
      this.hasPrevious = !response.first;
      this.hasNext = !response.last;
      console.log('Posts loaded:', this.posts);
    });
  }

  nextPage() {
    if (this.hasNext) this.loadPosts(this.currentPage + 1);
  }

  previousPage() {
    if (this.hasPrevious) this.loadPosts(this.currentPage - 1);
  }


  addComment(postId: number, commentText: string): void {
    this.commentService.addComment(postId, commentText).subscribe({
      next: () => {
        window.location.reload();
      },
      error: (err) => console.error('Error al añadir comentario', err)
    });

  }

  deleteComment(postId: number, commentId: number): void {
    this.commentService.deleteComment(commentId).subscribe(() => {
      const post = this.posts.find(p => p.id === postId);
      if (post && post.comments) {
        post.comments = post.comments.filter(c => c.id !== commentId);
      };
      this.loadPosts(this.currentPage);
    });
  }




  createPost(): void {
    if (!this.newPost.title.trim() || !this.newPost.content.trim()) {
      alert('Por favor, completa los campos obligatorios');
      return;
    }

    const formData = new FormData();
    formData.append('title', this.newPost.title);
    formData.append('content', this.newPost.content);
    formData.append('tag', this.newPost.tag || '');

    if (this.newPost.image) {
      formData.append('image', this.newPost.image, this.newPost.image.name);
    }

    this.postService.createPost(formData).subscribe({
      next: (post: any) => {
        this.posts.unshift(post);
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
        alert('Post generado exitosamente por IA');

        this.router.navigate(['/']); // Navigate to the new post
        this.aiPostTag = ''; // Reset form
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


