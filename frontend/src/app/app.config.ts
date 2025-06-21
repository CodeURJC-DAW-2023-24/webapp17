import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { Home } from './pages/home/home';
import { LogIn } from './pages/login/login';
import { Error } from './pages/error/error';
import { NoAdmin } from './pages/noadmin/noadmin';
import { NoPermission } from './pages/nopermission/nopermission';
import { MyPosts } from './pages/mypost/myposts';
import { EditPost } from './pages/editpost/editpost';
import { Statistics } from './pages/stats/statistics';
import { Contacto } from './pages/contacto/contacto';
import { Admin } from './pages/admin/admin';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter([
      { path: '', component: Home },                        // Home page
      { path: 'login', component: LogIn },                   // Login page
      { path: 'myposts', component: MyPosts },               // User's posts
      { path: 'editpost', component: EditPost },             // Create post
      { path: 'editpost/:id', component: EditPost },         // Edit post with ID
      { path: 'statistics', component: Statistics },     // Statistics
      {path: 'admin', component: Admin},                     // Admin page 
      { path: 'noadmin', component: NoAdmin },               // No admin access
      { path: 'nopermission', component: NoPermission },     // No permission
      { path: 'error', component: Error },
      {path: 'contact', component: Contacto}       , // Contact page
      { path: '**', redirectTo: 'error' }                   // 404 redirect
    ])
  ]
};