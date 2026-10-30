import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Scanner } from './einlasskontrolle/scanner/scanner';
import { Ergebnis } from './einlasskontrolle/ergebnis/ergebnis';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'scanner', component: Scanner },

  {
    path: 'vorschau/erfolg',
    component: Ergebnis,
    data: {
      ergebnis: {
        typ: 'erfolg',
        saal: 2,
        reihe: 5,
        sitzplatz: 8
      }
    }
  },

  {
    path: 'vorschau/fehler',
    component: Ergebnis,
    data: {
      ergebnis: {
        typ: 'fehler',
        titel: 'Bereits entwertet',
        beschreibung: 'Diese Kinokarte wurde bereits entwertet.'
      }
    }
  }
];
