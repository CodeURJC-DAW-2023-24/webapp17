import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [CommonModule,RouterModule], 
  standalone: true,
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header implements OnInit {
  
  isAdmin: boolean=  false;
  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    
  }

  constructor(private authService: AuthService) {}

  get currentUser() {
    return this.authService.currentUser$;
  }

 

}
