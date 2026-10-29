import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Scanner } from './einlasskontrolle/scanner/scanner';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'scanner', component: Scanner }
];
