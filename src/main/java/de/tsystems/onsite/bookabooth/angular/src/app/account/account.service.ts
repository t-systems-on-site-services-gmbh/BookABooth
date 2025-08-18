import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface Account {
  login: string;
  authorities: string[];
}

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private accountSubject: BehaviorSubject<Account | null> = new BehaviorSubject<Account | null>(null);
  public authenticatedSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) {}

  retrieveAccount(): Observable<Account | null> {
    return this.http.get<Account>('/api/account').pipe(
      map(account => {
        this.accountSubject.next(account);
        this.authenticatedSubject.next(!!account);
        return account;
      }),
      catchError(() => {
        this.accountSubject.next(null);
        this.authenticatedSubject.next(false);
        return of(null);
      }),
    );
  }

  get account$(): Observable<Account | null> {
    return this.accountSubject.asObservable();
  }

  get authenticated$(): Observable<boolean> {
    return this.authenticatedSubject.asObservable();
  }

  get authenticated(): boolean {
    return this.authenticatedSubject.getValue();
  }

  get userAuthorities(): string[] {
    return this.accountSubject.getValue()?.authorities || [];
  }

  hasAnyAuthority(authorities: string[] | string): boolean {
    const userAuthorities = this.userAuthorities;
    if (!userAuthorities || userAuthorities.length === 0) {
      return false;
    }
    if (typeof authorities === 'string') {
      return userAuthorities.includes(authorities);
    }
    return authorities.some(authority => userAuthorities.includes(authority));
  }

  authenticate(): Observable<any> {
    return this.http.get('/api/authenticate');
  }
}
