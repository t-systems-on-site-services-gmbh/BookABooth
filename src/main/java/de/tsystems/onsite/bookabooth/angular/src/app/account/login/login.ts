import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  authenticationError = false;
  username = 'test';
  password = '';

  constructor(
    private loginService: LoginService,
    private router: Router,
  ) {}

  onSubmit() {
    this.loginService.doLogin(this.username, this.password).subscribe({
      next: () => {
        this.authenticationError = false;
        this.loginService.hideLogin();
        this.router.navigate(['/']);
      },
      error: () => {
        this.authenticationError = true;
      },
    });
  }
}
