import { Component } from '@angular/core';
import { Login } from '../account/login/login';
import { LoginService } from '../account/login.service';
import { AccountService } from '../account/account.service';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [Login],
  templateUrl: './homepage.html',
  styleUrl: './homepage.scss',
})
export class Homepage {
  constructor(private accountService: AccountService) {}

  ngOnInit() {
    this.accountService.retrieveAccount().subscribe();
  }
}
