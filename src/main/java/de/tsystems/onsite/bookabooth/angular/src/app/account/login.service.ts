import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private http: HttpClient) {}

  doLogin(username: string, password: string): Observable<any> {
    const body = new HttpParams().set('username', username).set('password', password).set('submit', 'Login');
    return this.http.post('api/authentication', body, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    });
  }

  hideLogin() {
    // Logic to hide the login modal or component
    console.log('Login modal hidden');
  }
}
