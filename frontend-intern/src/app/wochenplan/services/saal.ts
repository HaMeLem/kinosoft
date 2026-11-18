import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface Saal {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class SaalService {

  // Vorläufige Saaldaten, bis ein Backend-Endpunkt verfügbar ist.
  holeSaele(): Observable<Saal[]> {
    return of([
      { id: 1, name: 'Kleiner Saal' },
      { id: 2, name: 'Großer Saal' },
    ]);
  }
}
