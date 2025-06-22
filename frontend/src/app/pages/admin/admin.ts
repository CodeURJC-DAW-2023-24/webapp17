import { Component, OnInit, Inject } from '@angular/core';
import { IssueService } from '../../services/issue.service';
import { UsersService, UserInfoDTO, CreateUserRequestDTO, ApiResponse } from '../../services/user.services';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.html',
  imports: [FormsModule,CommonModule],
  styleUrls: ['./admin.css']  
})
export class Admin implements OnInit {

  issues: any[] = [];
  users: UserInfoDTO[] = [];

  newUser: CreateUserRequestDTO = {
    username: '',
    email: '',
    password: '',
    role: 'USER'
  };

  loadingIssues = false;
  loadingUsers = false;
  errorMessage = '';

  constructor(
    @Inject(IssueService) private issueService: IssueService,
    private usersService: UsersService
  ) { }

  ngOnInit(): void {
    this.loadIssues();
    this.loadUsers();
  }

  loadIssues(): void {
    this.loadingIssues = true;
    this.issueService.getAllIssues().subscribe({
      next: (data: any[]) => {
        this.issues = data;
        this.loadingIssues = false;
      },
      error: (err:any) => {
        this.errorMessage = 'Error cargando avisos';
        this.loadingIssues = false;
      }
    });
  }

  loadUsers(): void {
    this.loadingUsers = true;
    this.usersService.getAllUsers().subscribe({
      next: (data: UserInfoDTO[] | string) => {
        if (typeof data === 'string') {
          this.errorMessage = data;
          this.users = [];
        } else {
          this.users = data;
        }
        this.loadingUsers = false;
      },
      error: (err: any) => {
        this.errorMessage = 'Error cargando usuarios';
        this.loadingUsers = false;
      }
    });
  }

  deleteIssue(id: number): void {
    if (!confirm('¿Seguro que quieres borrar este aviso?')) return;

    this.issueService.deleteIssue(id).subscribe({
      next: () => {
        this.issues = this.issues.filter(issue => issue.id !== id);
      },
      error: () => alert('Error borrando el aviso')
    });
  }

  deleteUser(id: number): void {
    if (!confirm('¿Seguro que quieres borrar este usuario?')) return;

    this.usersService.deleteUser(id).subscribe({
      next: (response: ApiResponse) => {
        if (response.error) {
          alert('Error: ' + response.error);
        } else {
          this.users = this.users.filter(user => user.id !== id);
        }
      },
      error: () => alert('Error borrando usuario')
    });
  }

  createUser(): void {
    if (!this.newUser.username || !this.newUser.email || !this.newUser.password) {
      alert('Rellena todos los campos');
      return;
    }

    this.usersService.createUser(this.newUser).subscribe({
      next: (message: string) => {
        alert(message || 'Usuario creado');
        this.loadUsers();
        this.resetForm();
      },
      error: (error: any) => {
        const errMsg = this.usersService.getErrorMessage(error);
        alert('Error creando usuario: ' + errMsg);
      }
    });
  }

  resetForm(): void {
    this.newUser = {
      username: '',
      email: '',
      password: '',
      role: 'USER'
    };
  }
}
