import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  const clonedRequest = req.clone({ withCredentials: true });
  return next(clonedRequest);
};


export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  console.log('HTTP Request:', req);

  const clonedRequest = req.clone({ withCredentials: true });

  return next(clonedRequest).pipe(
    tap(event => {
      if (event instanceof HttpResponse) {
        console.log('HTTP Response Body:', event.body);
      }
    })
  );
};

