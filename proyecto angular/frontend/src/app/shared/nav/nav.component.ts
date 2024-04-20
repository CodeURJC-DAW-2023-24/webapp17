import { CommonModule } from '@angular/common';
import { Component , OnDestroy, OnInit} from '@angular/core';
import { LoginService } from '../../services/auth/login.service';
import { User } from '../../services/auth/user';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent implements OnInit, OnDestroy {
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
