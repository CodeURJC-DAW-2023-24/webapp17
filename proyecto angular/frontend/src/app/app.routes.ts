import { Routes } from '@angular/router';
import { HeaderComponent } from './shared/header/header.component';
import { FooterComponent } from './shared/footer/footer.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { NavComponent } from './shared/nav/nav.component';
import { LoginComponent } from './auth/login/login.component';
import { ContactoComponent } from './pages/contacto/contacto.component';

export const routes: Routes = [
    {path: '',redirectTo:'/inicio',pathMatch:'full'},
    {path: 'inicio',component: DashboardComponent},

    {path: 'iniciar-sesion', component: LoginComponent},
    {path: 'contacto', component: ContactoComponent},


];