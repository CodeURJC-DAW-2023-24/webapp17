import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { credentialsInterceptor, loggingInterceptor } from './app/interceptors/credentials.interceptor';

bootstrapApplication(App, {
  providers: [
    provideHttpClient(withInterceptors([credentialsInterceptor, loggingInterceptor])),
    ...appConfig.providers
    ]
}).catch(err => console.error(err));