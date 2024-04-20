import { Component } from '@angular/core';
import { HeaderComponent } from '../../shared/header/header.component';
import { NavComponent } from '../../shared/nav/nav.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import { FormBuilder, ReactiveFormsModule , Validators} from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../../services/auth/login.service';
import { LoginRequest } from '../../services/auth/loginRequest';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [HeaderComponent, NavComponent, FooterComponent,ReactiveFormsModule,HttpClientModule,CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginError: string="";
  loginForm = this.formBuilder.group({
    email:['prueba@gmail.com', [Validators.required, Validators.email]],
    password:['', Validators.required],
  })

  constructor(private formBuilder: FormBuilder, private router:Router, private loginService: LoginService) { }

  login(){
    if(this.loginForm.valid){
      this.loginService.login(this.loginForm.value as LoginRequest).subscribe({
        next: userData => {
          console.log(userData);
          
        },
        error: errorData => {
          console.error(errorData);
          this.loginError=errorData;
        },
        complete:() => {
          console.info("Login completado");
          this.router.navigateByUrl('/inicio');
          this.loginForm.reset();
        }
      })


    }else{
      this.loginForm.markAllAsTouched();
    }
  }
  get email(){
    return this.loginForm.controls.email;

  }

  get password(){
    return this.loginForm.controls.password;
  }

}
