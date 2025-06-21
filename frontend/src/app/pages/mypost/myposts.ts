import { Component } from '@angular/core';
import { Post } from '../../models/post';
import { PostService } from '../../services/post.service';
import { CommonModule } from '@angular/common';
import { CommentService } from '../../services/comment.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-my-posts',
  imports: [CommonModule,FormsModule], standalone: true,
  templateUrl: './myposts.html',
  styleUrl: './myposts.css'
})
export class MyPosts {
  posts: Post[] = [];
  currentPage = 0;
  size = 5;
  hasNext = false;
  hasPrevious = false;
  commentsVisible: { [postId: number]: boolean } = {};
  newComments: { [postId: number]: string } = {};

  constructor(private postService: PostService, private commentService :  CommentService) { }

  ngOnInit(): void {
    this.loadPosts(this.currentPage);
  }

loadPosts(page: number): void {
  this.postService.getPosts(page, this.size).subscribe(posts => {
    this.posts = posts;
    this.currentPage = page;
    this.hasPrevious = page > 0;
    this.hasNext = posts.length === this.size; // si devuelve el tamaño completo, puede haber más
  });
}

  toggleComments(postId: number) {
    this.commentsVisible[postId] = !this.commentsVisible[postId];
  }

  submitComment(postId: number) {
    const commentText = this.newComments[postId];
    if (!commentText || commentText.trim().length === 0) return;

    this.commentService.addComment(postId, commentText).subscribe(() => {
      this.newComments[postId] = '';
      this.loadPosts(this.currentPage);  // recarga para ver nuevo comentario
    });
  }

  nextPage() {
    if (this.hasNext) this.loadPosts(this.currentPage + 1);
  }

  previousPage() {
    if (this.hasPrevious) this.loadPosts(this.currentPage - 1);
  }

}




