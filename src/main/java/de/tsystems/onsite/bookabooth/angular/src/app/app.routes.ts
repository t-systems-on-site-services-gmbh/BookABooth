import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Homepage } from './homepage/homepage';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
  },
  {
    path: '',
    component: Homepage,
  },
];
