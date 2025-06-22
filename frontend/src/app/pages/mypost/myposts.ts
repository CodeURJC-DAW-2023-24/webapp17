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
  selector: 'app-myposts',
  imports: [ReactiveFormsModule, CommonModule, FormsModule, RouterModule, CustomDatePipe], standalone: true,
  templateUrl: './myposts.html',
  styleUrl: './myposts.css'
})
export class MyPosts implements OnInit {
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

  // Comments functionality
  commentText: string = '';
  visibleComments: Set<number> = new Set();
  currentUser$!: Observable<User | null>;


  constructor(
    private postService: PostService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private commentService: CommentService
  ) { }

  ngOnInit(): void {
    // Initialize user data
    this.currentUser$ = this.authService.currentUser$;

    this.loadMyPosts(this.currentPage);

  }

  editPost(postId: number): void {
    this.router.navigate(['/editpost', postId]);
  }


  // Posts loading and pagination
  loadMyPosts(page: number): void {
    this.postService.getMyPosts(page, this.size).subscribe(response => {
      this.posts = response.content;
      this.currentPage = page;
      this.totalPages = response.totalPages;
      this.hasPrevious = !response.first;
      this.hasNext = !response.last;
      console.log('Posts loaded:', this.posts);
    });
  }

  nextPage() {
    if (this.hasNext) this.loadMyPosts(this.currentPage + 1);
  }

  previousPage() {
    if (this.hasPrevious) this.loadMyPosts(this.currentPage - 1);
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
      this.loadMyPosts(this.currentPage);
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


}


