import { Component } from '@angular/core';
import { Post } from '../../models/post';
import { PostService } from '../../services/post.service';
import { CommonModule } from '@angular/common';
import { CommentService } from '../../services/comment.service';
import { FormsModule } from '@angular/forms';
import { CustomDatePipe } from "../../Pipes/custom-date.pipe";

@Component({
  selector: 'app-my-posts',
  imports: [CommonModule, FormsModule, CustomDatePipe], standalone: true,
  templateUrl: './myposts.html',
  styleUrl: './myposts.css'
})
export class MyPosts {
  posts: Post[] = [];
  currentPage = 0;
  totalPages = 0;
  size = 3;
  hasNext = false;
  hasPrevious = false;
  commentsVisible: { [postId: number]: boolean } = {};
  newComments: { [postId: number]: string } = {};

  constructor(private postService: PostService, private commentService: CommentService) { }

  ngOnInit(): void {
    this.loadMyPosts(this.currentPage);
  }

  loadMyPosts(page: number): void {
    this.postService.getMyPosts(page, this.size).subscribe(response => {
      this.posts = response.content;
      this.currentPage = page;
      this.totalPages = response.totalPages;
      this.hasPrevious = !response.first;
      this.hasNext = !response.last;
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
      this.loadMyPosts(this.currentPage);  // recarga para ver nuevo comentario
    });
  }

  nextPage() {
    if (this.hasNext) this.loadMyPosts(this.currentPage + 1);
  }

  previousPage() {
    if (this.hasPrevious) this.loadMyPosts(this.currentPage - 1);
  }

}




