import { Component } from '@angular/core';
import { IssueService } from '../../services/issue.service';
import { Issue } from '../../models/issue';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-contacto',
  templateUrl: './contacto.html',
  styleUrls: ['./contacto.css'],
  imports: [FormsModule, CommonModule]
})
export class Contacto {
  issue: Issue = {
    name: '',
    email: '',
    content: ''
  };

  enviado = false;

  constructor(private issueService: IssueService, private router: Router) { }

  onSubmit(): void {
    this.issueService.createIssue(this.issue).subscribe({
      next: () => {
        this.enviado = true;
        this.issue = { name: '', email: '', content: '' };
        alert('Mensaje enviado correctamente');
        this.router.navigate(['/contact']);
      },
      error: err => {
        this.enviado = true;
        this.issue = { name: '', email: '', content: '' };
        this.router.navigate(['/contact']);
      }
    });
  }
}
