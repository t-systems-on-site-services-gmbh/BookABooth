import { Component } from '@angular/core';
import { Login } from '../account/login/login';
import { LoginService } from '../account/login.service';
import { AccountService, Account } from '../account/account.service';
import { map, Observable } from 'rxjs';
import { AsyncPipe, CommonModule } from '@angular/common';
import { AdminDashboard } from '../admin/admin.dashboard/admin.dashboard';
import { Checklist } from '../user/checklist/checklist';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [Login, AsyncPipe, CommonModule, AdminDashboard, Checklist],
  templateUrl: './homepage.html',
  styleUrl: './homepage.scss',
})
export class Homepage {
  account$: Observable<Account | null>;

  constructor(
    private loginService: LoginService,
    private accountService: AccountService,
  ) {
    this.account$ = this.accountService.account$.pipe(map(account => account || null));
    console.log(this.account$);
  }

  ngOnInit() {
    this.accountService.retrieveAccount().subscribe();
  }

  logout() {
    this.loginService.logout().subscribe({
      next: () => {
        this.accountService.clearAccount();
      },
    });
  }

  isLoggedIn(): boolean {
    return this.accountService.getAccount() !== null;
  }
}
