import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Scanner } from './einlasskontrolle/scanner/scanner';
import { Ergebnis } from './einlasskontrolle/ergebnis/ergebnis';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'scanner', component: Scanner },
  { path: 'ergebnis', component: Ergebnis },
];
