import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavComponent } from '../../shared/nav/nav.component';
import { LoginService } from '../../services/auth/login.service';
import { CommonModule } from '@angular/common';
import { User } from '../../services/auth/user';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NavComponent,CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit ,OnDestroy{
  userLoginOn: boolean = false;
  userData?: User ;
  constructor(private loginService: LoginService) {

  }
  ngOnDestroy(): void {

  }
 ngOnInit(): void {
   this.loginService.userLoginOn.subscribe({
     
     next:(userLoginOn) => {this.userLoginOn = userLoginOn}
   })
   this.loginService.userData.subscribe({
      
    next:(userData) => {this.userData = userData}
  })
 }

}
