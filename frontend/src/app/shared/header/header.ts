import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { User } from '../../models/user';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header implements OnInit {
  currentUser$!: Observable<User | null>; 
  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    this.currentUser$ = this.authService.currentUser$; 
  }
}
