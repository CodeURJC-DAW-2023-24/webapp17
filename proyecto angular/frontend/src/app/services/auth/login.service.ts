import { Injectable } from '@angular/core';
import { LoginRequest } from './loginRequest';
import { BehaviorSubject, Observable, catchError, throwError, tap } from 'rxjs';
import { HttpClient, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { User } from './user';


@Injectable({
  providedIn: 'root'
})
export class LoginService {
  currentUserLoginOn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false)
  currentUserData: BehaviorSubject<User> = new BehaviorSubject<User>({id:0,email:""} as User)
  constructor(private http:HttpClient) { }
  login(credentials:LoginRequest): Observable<User>{
    console.debug("Intentando iniciar sesión")
    return this.http.get<User>("././assets/data.json").pipe(
      tap((userData: User )=> {this.currentUserData.next(userData)
        this.currentUserLoginOn.next(true)
      }),
      catchError(this.handleError)
    )
}
    private handleError(error: HttpErrorResponse) {
      if (error.status===0){
        console.error("Se ha producido un error:",error.error)
      }
      else{
        console.error("Backend returned code ${error.status}, body was: ",error.error)
      }

      return throwError(()=> new Error("Algo falló por favor intentelo de nuevo"))
    }

get userData():Observable<User>{
  return this.currentUserData.asObservable();
}
get userLoginOn():Observable<boolean>{
  return this.currentUserLoginOn.asObservable();

}
}


