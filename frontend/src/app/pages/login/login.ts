import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-login',
  styleUrls: ['./login.css'],
  imports: [FormsModule,CommonModule],
  standalone: true,
  templateUrl: './login.html'
})
export class LogIn {
  email = '';
  password = '';
  loginError: string | null = null;

  constructor(public authService: AuthService) {}

  onLogin(): void {
    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.loginError = null;
      },
      error: err => {
        this.loginError = 'Credenciales incorrectas o error del servidor';
      }
    });
  }
  
  onLogout(): void {
  console.log('onLogout llamado');
  this.authService.logout().subscribe({
    next: () => {
      console.log('Ejecutando logout');
    },
    error: (err) => {
      console.error('Error en logout:', err);
    }
  });
}

}
