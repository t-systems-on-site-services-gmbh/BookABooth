import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private apiUrl = '/api/authentication';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    const body = new HttpParams().set('username', username).set('password', password);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
    });
    return this.http.post(this.apiUrl, body.toString(), { headers });
  }

  openLogin(): void {
    // Modal öffnen
    const modal = document.getElementById('loginModal');
    if (modal) {
      // Bootstrap 5 Modal-Instanz holen und schließen
      // @ts-ignore
      const bsModal = bootstrap.Modal.getInstance(modal) || new bootstrap.Modal(modal);
      bsModal.show();
    }
  }

  hideLogin(): void {
    // Modal schließen
    const modal = document.getElementById('loginModal');
    if (modal) {
      // Bootstrap 5 Modal-Instanz holen und schließen
      // @ts-ignore
      const bsModal = bootstrap.Modal.getInstance(modal) || new bootstrap.Modal(modal);
      bsModal.hide();
    }
  }

  logout(): Observable<any> {
    return this.http.post('/api/logout', {});
  }
}
