import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PostService, PostDTO} from '../../services/post.service';

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

    const postData: PostDTO = {
      title: this.postForm.value.title,
      content: this.postForm.value.content,
      tag: this.postForm.value.tag,
      image: this.selectedImage ?? undefined
    };

    this.postService.updatePost(this.postId, postData).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/posts', this.postId]); // Redirige a la vista del post actualizado
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Error al actualizar el post.';
      }
    });
  }
}
