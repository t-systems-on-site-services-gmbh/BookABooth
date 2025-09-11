import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface Account {
  user: {
    id: number;
    login: string;
    email: string;
    firstName: string;
    lastName: string;
  };
  company: any;
  booking: any;
  authorities: string[];
  phoneNumber: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private accountSubject: BehaviorSubject<Account | null> = new BehaviorSubject<Account | null>(null);
  public account$: Observable<Account | null> = this.accountSubject.asObservable();

  constructor(private http: HttpClient) {}

  retrieveAccount(): Observable<Account | null> {
    return this.http.get<Account>('/api/account').pipe(
      map(account => {
        this.accountSubject.next(account);
        return account;
      }),
      catchError(() => {
        this.accountSubject.next(null);
        return of(null);
      }),
    );
  }

  clearAccount() {
    this.accountSubject.next(null);
  }

  getAccount(): Account | null {
    return this.accountSubject.value;
  }
}
