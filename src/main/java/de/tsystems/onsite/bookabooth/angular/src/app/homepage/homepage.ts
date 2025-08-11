import { Component } from '@angular/core';
import { Login } from '../login/login';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [Login],
  templateUrl: './homepage.html',
  styleUrl: './homepage.scss',
})
export class Homepage {}
