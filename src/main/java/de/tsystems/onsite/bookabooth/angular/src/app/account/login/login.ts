import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AccountService } from '../account.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(
    private loginService: LoginService,
    private accountService: AccountService,
    private router: Router,
    private http: HttpClient,
  ) {}

  onSubmit() {
    const body = new HttpParams().set('username', this.username).set('password', this.password);

    this.loginService.login(this.username, this.password).subscribe({
      next: () => {
        this.loginService.hideLogin();
        this.accountService.retrieveAccount().subscribe();
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        this.errorMessage = 'Login failed. Please try again.';
        console.error('Login error:', error);
      },
    });
  }
}
