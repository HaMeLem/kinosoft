import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Saal } from '../dtos/saal';

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
