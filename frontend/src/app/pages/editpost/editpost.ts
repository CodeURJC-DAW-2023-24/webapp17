import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PostService } from '../../services/post.service';
import { PostCreationDTO } from '../../models/post';

@Component({
  selector: 'app-edit-post',
  templateUrl: './editpost.html',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
})
export class EditPost implements OnInit {

  postId!: number;
  postForm!: FormGroup;
  selectedImage: File | null = null;
  loading = false;
  errorMessage = '';
  hovered = false;
  newPost = {
    title: '',
    content: '',
    tag: '',
    imageName: '',
    image: null as File | null
  };

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private fb: FormBuilder,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.postId = +this.route.snapshot.paramMap.get('id')!;
    this.postForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      tag: [''],
      image: [null]
    });

    this.loadPost();
  }

  loadPost(): void {
    this.postService.getPostById(this.postId).subscribe({
      next: (post) => {
        this.postForm.patchValue({
          title: post.title,
          content: post.content,
          tag: post.tag
        });
      },
      error: () => {
        this.errorMessage = 'Error al cargar el post.';
      }
    });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedImage = file;
    }
  }




  onSubmit(): void {
    if (this.postForm.invalid) {
      return;
    }
    this.loading = true;

    const formData = new FormData();
    formData.append('title', this.postForm.get('title')?.value);
    formData.append('content', this.postForm.get('content')?.value);
    formData.append('tag', this.postForm.get('tag')?.value || '');

    if (this.selectedImage) {
      formData.append('image', this.selectedImage, this.selectedImage.name);
    }

    this.postService.updatePost(this.postId, formData).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/myposts']);
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/myposts']);
      }
    });
  }

}
