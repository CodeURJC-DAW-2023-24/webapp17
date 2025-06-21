import { Component } from '@angular/core';
import { IssueService } from '../../services/issue.service';
import { Issue } from '../../models/issue';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-contacto',
  templateUrl: './contacto.html',
  styleUrls: ['./contacto.css'],
  imports: [FormsModule,CommonModule]
})
export class Contacto {
  issue: Issue = {
    name: '',
    email: '',
    content: ''
  };

  enviado = false;

  constructor(private issueService: IssueService) {}

  onSubmit(): void {
    this.issueService.createIssue(this.issue).subscribe({
      next: () => {
        this.enviado = true;
        this.issue = { name: '', email: '', content: '' };
      },
      error: err => console.error('Error al enviar el mensaje:', err)
    });
  }
}
