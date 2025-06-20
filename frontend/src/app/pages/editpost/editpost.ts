import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PostService, PostDTO, PostResponseDTO } from '../../services/post.service';

@Component({
  selector: 'app-edit-post',
  templateUrl: './editpost.html',
  standalone: true,
  imports: [ReactiveFormsModule], // Añade CommonModule, ReactiveFormsModule si lo necesitas
})
export class EditPost implements OnInit {
  editForm: FormGroup;
  postId!: number;
  selectedImageFile: File | null = null;
  previewUrl: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService
  ) {
    this.editForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      tag: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadPostData();
  }

  loadPostData(): void {
    this.postService.getPost(this.postId).subscribe((post: PostResponseDTO) => {
      this.editForm.patchValue({
        title: post.title,
        content: post.content,
        tag: post.tag
      });

      // Si ya hay imagen, se podría mostrar (si la URL está accesible)
      if (post.image) {
        this.previewUrl = `http://localhost:8443/api/v1/posts/${this.postId}/image`; // ajusta si usas endpoint diferente
      }
    });
  }

  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.selectedImageFile = file;

      const reader = new FileReader();
      reader.onload = e => this.previewUrl = reader.result as string;
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.editForm.invalid) return;

    const post: PostDTO = this.editForm.value;
    const formData = new FormData();
    formData.append('title', post.title);
    formData.append('content', post.content);
    formData.append('tag', post.tag);
    if (this.selectedImageFile) {
      formData.append('image', this.selectedImageFile);
    }

    this.postService.updatePost2(this.postId, formData).subscribe({
      next: () => this.router.navigate(['/posts', this.postId]),
      error: err => console.error('Error al actualizar el post:', err)
    });
  }
}
